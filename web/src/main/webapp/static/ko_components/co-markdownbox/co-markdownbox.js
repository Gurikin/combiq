define(['ajax'], function(ajax) {

    function ViewModel(params) {
        this.text = ko.wrap(params.text);
        this.active=ko.wrap('markdown');
        this.preview=ko.wrap();
    }
    ViewModel.prototype.toggleSrc=function() {
    this.active('markdown');
                };
    ViewModel.prototype.togglePreview=function() {
    this.active('html');
    var self=this;
    $.ajax({
                        url: '/markdown/preview',
                        datatype: 'text',
                        contentType: 'text/plain',
                        data: self.text(),
                        method: 'POST',
                        success: function(result) {
                            self.preview(result);
                        }
                    });
                    }
    return ViewModel;
});
