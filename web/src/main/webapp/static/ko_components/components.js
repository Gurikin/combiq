(function() {
    function getComponentDefinitions(directory, componentName, templateConfig) {
        var styles;
        if (templateConfig && templateConfig.styles) {
            styles = {
                engine: templateConfig.styles,
                path: require.toUrl(directory + '/' + componentName + '.' + templateConfig.styles)
            };
        }

        if (templateConfig && templateConfig.layoutSeparated) {
            return {
                viewModel: {require: require.toUrl(directory + '/' + componentName + '.js')},
                template: {require: 'text!' + require.toUrl(directory + '/' + componentName + '.html')},
                styles: styles
            }
        } else {
            return {
                require: require.toUrl(directory + '/' + componentName + '.js'),
                styles: styles
            }
        }
    }

    function define(tagName, directory, componentName, templateConfig) {
        document.createElement(componentName);
        var definition = getComponentDefinitions(directory, componentName, templateConfig);
        ko.components.register(tagName, definition);
    }

    define('co-helloworld', 'ko_components/co-helloworld', 'co-helloworld', {layoutSeparated: true});
    define('co-jobsubscribe', 'ko_components/co-jobsubscribe', 'co-jobsubscribe', {layoutSeparated: true});
    define('co-commentposter', 'ko_components/co-commentposter', 'co-commentposter', {layoutSeparated: true, styles: 'css'});

    ko.createComponent = function(componentName, params, tag) {
        var html;
        if (!tag) html = $("<!-- ko component: { name: '" + componentName + "', params: $data } --><!-- /ko -->");
        else html = $("<" + tag + " data-bind=\"component: { name: '" + componentName + "', params: $data }\"></" + tag +">");
        ko.applyBindings(params, html.get()[0]);
        return html;
    };

    ko.components.defaultLoader.originalLoadComponent = ko.components.defaultLoader.loadComponent;
    ko.components.defaultLoader.loadComponent = function(componentName, config, callback) {
        if (config && config.styles) {
            console.log('load:', config.styles.engine);
            require([config.styles.engine], function() {
                require(['requirejs.' + config.styles.engine + '!' + config.styles.path], function() {
                    // Nothing to do.
                });
                ko.components.defaultLoader.originalLoadComponent(componentName, config, callback);
            });
        } else {
            ko.components.defaultLoader.originalLoadComponent(componentName, config, callback);
        }
    };
})();