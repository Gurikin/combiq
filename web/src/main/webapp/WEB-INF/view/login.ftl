<#import "templates.ftl" as templates />

<@templates.layoutBody head=head>
    <div class="container">
        <div>
            <a href="https://github.com/login/oauth/authorize?client_id=${githubClientId}&scope=user">
                Login via Github.com
            </a>
        </div>
    </div>
</@templates.layoutBody>