define(['knockout', 'js/lib/dropzone', 'requirejs.css!js/lib/dropzone'], function(ko, Dropzone) {

    function ViewModel() {
        var self = this;

        this.dropzoneElement = ko.wrap();
        this.buttons = [];
        this.align = 'top';

        this.init = function() {
            var dropZone = new Dropzone($(this.dropzoneElement()).get(0), {
                url: '/rest/v1/markdown/image',
                maxFiles: 1
            });
            dropZone.on('success', function(file, response) {
                self.dialog.result(response.location);
                self.dialog.close();
            });
        };
    }

    return ViewModel;
});