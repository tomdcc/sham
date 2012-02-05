<!doctype html>
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

	<title>Sham Herald</title>
	<meta name="description" content="">
	<meta name="author" content="">

	<meta name="viewport" content="width=device-width,initial-scale=1">

	<!-- CSS concatenated and minified via ant build script-->
	%{--<link rel="stylesheet" href="/css/main.css">--}%
	<!-- end CSS-->

	%{--<script src="js/libs/modernizr-2.0.6.min.js"></script>--}%

	<r:require module="app"/>
	<r:layoutResources/>

</head>

<body>

<div id="container">
	<header>
		<h1>Sham Herald</h1>
	</header>
	<div id="main" role="main">
		<g:layoutBody />
	</div>
	<footer>

	</footer>
</div> <!--! end of #container -->

<r:layoutResources/>

%{--<script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>--}%
%{--<script>window.jQuery || document.write('<script src="js/libs/jquery-1.6.2.min.js"><\/script>')</script>--}%

</body>
</html>
