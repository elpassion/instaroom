<#macro application user>
<!doctype html>
<!--
  Material Design Lite
  Copyright 2015 Google Inc. All rights reserved.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License
-->
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="A front-end template that helps you build fast, modern mobile web apps.">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
    <title>Instaroom</title>

    <!-- Add to homescreen for Chrome on Android -->
    <meta name="mobile-web-app-capable" content="yes">

    <!-- Add to homescreen for Safari on iOS -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-title" content="Instaroom">

    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.cyan-light_blue.min.css">
    <link rel="stylesheet" href="../static/styles.css">
    <style>
        #view-source {
            position: fixed;
            display: block;
            right: 0;
            bottom: 0;
            margin-right: 40px;
            margin-bottom: 40px;
            z-index: 900;
        }
    </style>
</head>
<body>
<div class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header">
    <header class="demo-header mdl-layout__header mdl-color--grey-100 mdl-color-text--grey-600">
        <div class="mdl-layout__header-row">
            <span class="mdl-layout-title">Instaroom</span>
            <div class="mdl-layout-spacer"></div>

            <button class="mdl-button mdl-js-button mdl-js-ripple-effect mdl-button--icon" id="hdrbtn">
                <i class="material-icons">more_vert</i>
            </button>
            <ul class="mdl-menu mdl-js-menu mdl-js-ripple-effect mdl-menu--bottom-right" for="hdrbtn">
                <li class="mdl-menu__item">About</li>
                <li class="mdl-menu__item">Contact</li>
            </ul>
        </div>
    </header>
    <div class="demo-drawer mdl-layout__drawer mdl-color--blue-grey-900 mdl-color-text--blue-grey-50">
        <header class="demo-drawer-header">
            <!--<img src="images/user.jpg" class="demo-avatar">-->
            <!--<div class="demo-avatar-dropdown">-->
            <!--<span>hello@example.com</span>-->
            <!--<div class="mdl-layout-spacer"></div>-->
            <!--<button id="accbtn" class="mdl-button mdl-js-button mdl-js-ripple-effect mdl-button&#45;&#45;icon">-->
            <!--<i class="material-icons" role="presentation">arrow_drop_down</i>-->
            <!--<span class="visuallyhidden">Accounts</span>-->
            <!--</button>-->
            <!--<ul class="mdl-menu mdl-menu&#45;&#45;bottom-right mdl-js-menu mdl-js-ripple-effect" for="accbtn">-->
            <!--<li class="mdl-menu__item">hello@example.com</li>-->
            <!--<li class="mdl-menu__item">info@example.com</li>-->
            <!--<li class="mdl-menu__item"><i class="material-icons">add</i>Add another account...</li>-->
            <!--</ul>-->
            <!--</div>-->
        </header>
        <nav class="demo-navigation mdl-navigation mdl-color--blue-grey-800">
            <a class="mdl-navigation__link" href="/"><i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">home</i>Home</a>
            <a class="mdl-navigation__link" href="/login"><i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">people</i>Sign-in</a>
            <!--<div class="mdl-layout-spacer"></div>-->
            <!--<a class="mdl-navigation__link" href=""><i class="mdl-color-text&#45;&#45;blue-grey-400 material-icons" role="presentation">help_outline</i><span class="visuallyhidden">Help</span></a>-->
        </nav>
    </div>
    <main class="mdl-layout__content mdl-color--grey-100">
        <div class="mdl-grid demo-content">
            <#nested>
        </div>
    </main>
</div>

<a href="https://github.com/elpassion/instaroom" target="_blank" id="view-source" class="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white">View Source</a>
<script src="https://code.getmdl.io/1.3.0/material.min.js"></script>
</body>
</html>
</#macro>