define(['knockout', 'ajax'], function(ko, ajax) {

    function ViewModel(params) {
        var self = this;
        this.postId = ko.wrap(params.postId);

        this.title = ko.wrap();
        this.content = ko.wrap();
        this.preview = ko.wrap();
        this.published = ko.wrap();

        console.log('this.postId()', this.postId());

        if (this.postId()) {
            var url = coUtils.getRestUrl('/rest/v1/post/{postId}', {
                postId: this.postId()
            });

            ajax
                .rest('GET', url)
                .done(function(post) {
                    self.title(post.title);
                    self.content(post.content.markdown);
                    self.preview(post.preview.markdown);
                    self.published(post.published);
                })
        }
    }

    ViewModel.prototype.save = function() {
        var self = this;

        var method = this.postId() ? 'PUT' : 'POST';

        var url = this.postId()
            ? coUtils.getRestUrl('/rest/v1/post/{postId}', {postId: this.postId()})
            : '/rest/v1/post';

        var json = {
            title: this.title(),
            content: this.content(),
            preview: this.preview(),
            published: !!this.published()
        };

        ajax
            .rest(method, url, json)
            .done(function(data) {
                self.postId(data.id);

                new Dialog('Статья успешно сохранена');
            });
    };

    return ViewModel;
});