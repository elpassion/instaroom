<#import "layout.ftl" as layout>

<@layout.application user>

    <#list events as event>
        <div class="demo-charts mdl-color--white mdl-shadow--2dp mdl-cell mdl-cell--12-col mdl-grid">
            <p>${event}</p>
        </div>
    </#list>
</@layout.application>