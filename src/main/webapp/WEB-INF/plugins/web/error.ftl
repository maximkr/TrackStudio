<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<HTML>
<HEAD>
    <TITLE>TrackStudio</TITLE>
    <META NAME="Generator" CONTENT="EditPlus">
    <META NAME="Author" CONTENT="">
    <META NAME="Keywords" CONTENT="">
    <META NAME="Description" CONTENT="">
    <link rel="shortcut icon" href="${contextPath}/favicon.ico" type="image/x-icon"/>
</HEAD>
<style>
    div.title{
        font-family: Tahoma, sans-serif;
        color: #666666;
        font-size: 11px;
        font-weight: normal;
    }
    div.data{
        font-family: Tahoma, sans-serif;
        color: #000000;
        font-size: 20px;
        margin-top: 0px;
        padding-left: 20px;
    }
    BODY {
        font-family: Tahoma;
        font-size: 12px;
        padding: 8px 8px 8px 8px;
    }
    TABLE{
        border-collapse: collapse;
    }

    div.fullpath {
        padding: 4px 4px 4px 4px;
        vertical-align: middle;
        margin-left: 20px;
    }
    div.fullpath A {
        color: blue;
        font-weight: normal;
        padding-top: 4px;
        padding-bottom: 4px;
        padding-left: 2px;
        padding-right: 2px;
        text-decoration: underline;
        font-size: 11px;
    }
    div.fullpath A:hover{
        color: black;
        text-decoration: underline;
    }
    div.fullpath A.last {
        color: black;
        text-decoration: none;
    }

    table.products {
        border: #F3C76A 1px solid;
        background-color: #F8F8F8;
        width: 100%;
    }
    table.products TD {
        vertical-align: middle;
        border-right: #F8F8F8 3px solid;
    }
    table.products TD#active {
        border-right: #F3C76A 3px solid;
    }
    table.products caption{
        font-family: Tahoma, sans-serif;
        font-size: 12px;
        font-weight: bolder;
        background-color: #F8DBA1;
        text-align: left;
        padding-left: 20px;
        color: black;
    }
    table.products A {
        color: #000000;
        font-size: 12px;
    }
    table.taskList {
        font-family: Tahoma, sans-serif;
        color: #000000;
        font-size: 12px;
        vertical-align: middle;
        width: 100%;
        border: #cc9f85 1px solid;
        margin-bottom: 8px;
    }
    table.taskList caption{
        vertical-align: middle;
        font-family: Tahoma, sans-serif;
        color: #000000;
        text-align: left;
        font-weight: bolder;
        background-color: #decb9d;
        padding-left: 8px;
        padding-top: 4px;
        padding-bottom: 4px;
        border-top: #cc9f85 1px solid;
        border-left: #cc9f85 1px solid;
        border-right: #cc9f85 1px solid;
        margin-left: -1px;
        margin-right: 0;
        font-size: 12px;
        border-bottom: #cc9f85 1px solid;
        letter-spacing: 0;
    }
    table.taskList TH{
        color: #000000;
        text-align: left;
        font-weight: bold;
        background-color: #fcc671;

        vertical-align: middle;
        font-size: 12px;
    }
    table.taskList TD{
        background-color: #F8F8F8;
        font-size: 12px;
        text-align: left;
        font-weight: normal;
    }
    table.taskList TR.new TD{
        background-color: #f2ddac;
        font-size: 12px;
        font-style: italic;
    }

    table.taskList A{
        color: black;
        font-weight: bold;
        font-size: 12px;
    }
    table.taskList A.task{
        color: black;
        font-size: 12px;
        font-weight: bold;
        text-decoration: none;
    }

    table.common {
        width: 100%;
    }
    table.common TD {
        vertical-align: top;
    }

    span.date{
        color: black;
        font-weight: normal;
        font-size: 12px;
        font-family: Tahoma;
    }
    div.viewtask{
        padding: 4px 4px 4px 4px;
        margin-bottom: 8px;

    }
    div.viewtask span.number{
        font-size: 14px;
        font-family: Tahoma;
        color: #666666;
    }
    div.viewtask h1{
        font-size: 20px;
        font-family: Tahoma;
        color: black;
    }
    div.viewtask td{
        padding: 4px 4px 4px 4px;
    }
    div.viewtask td.submitted{
        font-size: 12px;
        font-family: Tahoma;
        color: #666666;
        text-align: left;

    }

    div.viewtask span.state{
        font-size: 12px;
        font-family: Tahoma;
        color: #666666;
        text-align: right;
        padding: 4px 4px 4px 4px;

    }

    div.viewtask span.user{
        color: black;
        font-weight: bold;
        font-size: 12px;
    }
    div.viewtask div.description{
        border: #C7D9E3 1px dashed;
        border-top: #C7D9E3 3px solid;
        padding: 4px 4px 4px 4px;
        background-color: #F8F8F8;
    }
    div.login {
        font-family: Tahoma, sans-serif;
        font-size: 11px;
        text-align: right;
        padding: 0px 0px 0px 0px;
        font-weight: normal;
    }
    div.login form{
        vertical-align: middle;
    }
    img.sitelogo{

    }
    td.tab{
        background-color: white;
        border-bottom: #F3C76A 3px solid;
        border-top: #F3C76A 1px dotted;
        color: black;
        border-left: #F3C76A 1px dotted;
        border-right: #F3C76A 1px dotted;
        white-space: nowrap;
        padding-left: 8px;
    }
    td.tab#active{
        border-bottom: white 1px solid;
        border-top: #F3C76A 3px solid;
    }
    td.tab A{
        text-decoration: none;
        color: black;
        font-size: 14px;
        font-weight: bold;
    }
    td.tabbottom{
        background-color: white;
        border-bottom: #F3C76A 3px solid;
    }
    td.controltop{
        background-color: white;
        border-top: #F3C76A 3px solid;
    }
    td.control{
        background-color: white;
        border-top: white 1px solid;
        border-bottom: #F3C76A 3px solid;
        color: black;
        border-left: #F3C76A 1px dotted;
        border-right: #F3C76A 1px dotted;
        white-space: nowrap;
        text-align: center;
        padding-bottom: 4px;
        padding-top: 2px;
    }

    div.sitesearch{
        float: right;
    }
    DIV.slider {
        width: 100%;
        font-family: Tahoma, Tahoma, sans-serif;
        font-size: 11px;
        background-color: #FFFFFF;
        text-align: center;
        color: #a90a08;
        vertical-align: top;
        font-weight: bold;
        padding-top: 2px;
        padding-bottom: 2px;
    }
    DIV.slider A {
        color: #000000;
        font-weight: bold;
        font-size: 11px;
        text-decoration: none;
        vertical-align: top;
    }
    DIV.slider SPAN {
        border: 1px solid #a90a08;
        padding-left: 2px;
        padding-right: 2px;
    }
    DIV.slider A:hover {
        color: #5E88BF;
        font-weight: bold;
        font-size: 11px;
        text-decoration: none;
        vertical-align: top;
    }
</style>
<BODY>
<#assign siteTitle="TrackStudio. An hierarchical issue management."/>
<br>
<h1>Page not found or has errors</h1>
</BODY>
</HTML>