var coSearch = {

    getSearchQuery: function() {
        return $('#searchBox').val() || '';
    },

    setSearchQuery: function(query) {
        $('#searchBox').val(query);
    }
};

var coMarkdown = {

    toHtml: function(markdown) {
        var deferred = new $.Deferred();

        require(['ajax'], function(ajax) {
            ajax
                .rest('POST', '/rest/v1/markdown', {markdown: ko.unwrap(markdown.markdown)})
                .done(function(response) {
                    deferred.resolve(response.html);
                });
        });

        return deferred.promise();
    }
};

var coUtils = {

    getRestUrl: function(template, params) {
        if (!params) {
            return template;
        }

        for (var key in params) {
            template = template.replace('{' + key + '}', encodeURIComponent(params[key]));
        }

        return template;
    }
};