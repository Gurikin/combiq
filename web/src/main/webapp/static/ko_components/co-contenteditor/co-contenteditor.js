define(['ajax'], function(ajax) {

    function ViewModel(params) {
        this.markdown = ko.wrap(params.markdown);
        this.html = ko.wrap(params.html);
        this.url = ko.unwrap(params.url);
        this.view = ko.wrap('preview'); // preview, edit

        this.editorMarkdown = ko.wrap();
        this.editorMarkdownModel = ko.wrap();
    }

    ViewModel.prototype.cancelClick = function() {
        this.view('preview');
    };

    ViewModel.prototype.applyClick = function() {
        var self = this;

        $.ajax({
            url: this.url,
            data: JSON.stringify({content: this.editorMarkdown()}),
            contentType: 'application/json',
            method: 'POST'
        }).done(function() {
            coMarkdown
                .toHtml({markdown: self.editorMarkdown()})
                .done(function(html) {
                    self.html(html);
                })
                .always(function() {
                    self.markdown(self.editorMarkdown());
                    self.view('preview');
                });
        });
    };

    ViewModel.prototype.previewClick = function() {
        this.editorMarkdown(this.markdown());
        this.view('edit');
        if (this.editorMarkdownModel()) {
            this.editorMarkdownModel().focus();
        }
    };

    ViewModel.prototype.keySend = function(data, event) {
        if (event.ctrlKey && (event.keyCode == 13 || event.keyCode == 10)) {
            this.applyClick();
        } else {
            return true;
        }
    };

    return ViewModel;
});