/**
 * to install gulp globally:
 * npm install -g gulp
 * 
* cd /drives/c/Users/mboparai/workspace/JEETest
* npm install --save-dev babel-core
* npm install --save-dev babel-preset-es2015
* npm install --save-dev gulp
* npm install --save-dev gulp-csso
* npm install --save-dev gulp-concat
* npm install --save-dev gulp-babel
* npm install --save-dev gulp-uglify
* npm install --save-dev gulp-uglifyes
* npm install --save-dev gulp-sourcemaps
* npm install --save-dev del
* gulp
*/

const gulp = require('gulp');
const minifyCSS = require('gulp-csso');
const concat = require("gulp-concat");
const babel = require('gulp-babel');
const uglify = require("gulp-uglify");
const uglifyES = require('gulp-uglifyes');
const sourcemaps = require("gulp-sourcemaps");
const del = require("del");

const paths = {
	/* we need to process files in this order */
	scripts: [
		'WebContent/resources/js/eninetworkgraph.base.js',
		'WebContent/resources/js/eninetworkgraph.benchmark.js',
		'WebContent/resources/js/eninetworkgraph.restapi.js',
		'WebContent/resources/js/eninetworkgraph.network.js',
		'WebContent/resources/js/eninetworkgraph.ui.tree.js',
		'WebContent/resources/js/eninetworkgraph.ui.setup.js',
		'WebContent/resources/js/eninetworkgraph.ui.events.js',
		'WebContent/resources/js/eninetworkgraph.external-link.js',
		"WebContent/resources/js/eninetworkgraph*.js"
	],
	vendorScripts: [
		'WebContent/resources/js/jquery-1.11.1.js',
		'WebContent/resources/js/jquery.mobile-1.4.5.js',
		'WebContent/resources/js/jquery-ui.js',
		'WebContent/resources/js/jquery-editable-select.js',
		'WebContent/resources/js/pekeUpload.js',
		'WebContent/resources/js/qunit-2.3.2.js',
		'WebContent/resources/js/vis.4.21.0.js',
		'WebContent/resources/js/jquery.fancytree.js',
		'WebContent/resources/js/notify.js',
		'WebContent/resources/js/js.cookie.js',
		'WebContent/resources/js/jquery.contextMenu.js',
		'WebContent/resources/js/select2.js',
		'WebContent/resources/js/jquery.svg.js',
		'WebContent/resources/js/spin.js'
	],
	css: ["WebContent/resources/css/eninetworkgraph*.css"],
	vendorCss: [
		'WebContent/resources/css/jquery-ui.css',
		'WebContent/resources/css/vis.css',
		'WebContent/resources/css/font-awesome.css',
		'WebContent/resources/css/jquery.contextMenu.css',
		'WebContent/resources/css/chosen.css',
		'WebContent/resources/css/select2.css',
		'WebContent/resources/css/jquery-editable-select.css',
		'WebContent/resources/css/jquery.svg.css',
		'WebContent/resources/css/custom.css',
		'',
	],
	scriptOutput: 'app.js',
	vendorScriptOutput: 'vendors.js',
	cssOutput: 'app.css',
	vendorCssOutput: 'vendors.css',
};

gulp.task('hello', function() {
  console.log('Hello Gulp');
});

gulp.task('css', function(){
  return gulp.src(paths.css)
    .pipe(minifyCSS())
    .pipe(concat(paths.cssOutput))
    /* output in same directory */
    .pipe(gulp.dest(function (file) {
        return file.base+'min/';
    }))
});

gulp.task('vendorcss', function(){
  return gulp.src(paths.vendorCss)
    .pipe(minifyCSS())
    .pipe(concat(paths.vendorCssOutput))
    /* output in same directory */
    .pipe(gulp.dest(function (file) {
        return file.base+'min/';
    }))
});

gulp.task('js', function(){
  return gulp.src(paths.scripts)
    .pipe(sourcemaps.init())
    .pipe(babel({
        presets: ['es2015']
    }))
    .pipe(uglify().on('error', function(e){
        console.log(e);
    }))
    /*.pipe(uglifyES({ 
       mangle: false, 
       ecma: 5
    }))*/
    .pipe(concat(paths.scriptOutput))
    .pipe(sourcemaps.write())
    /* output in same directory */
    .pipe(gulp.dest(function (file) {
        return file.base+'min/';
    }))
});

gulp.task('vendorjs', function(){
  return gulp.src(paths.vendorScripts)
    .pipe(sourcemaps.init())   
    /*.pipe(babel({
        presets: ['es2015']
    }))
    .pipe(uglify().on('error', function(e){
        console.log(e);
    }))*/  
    .pipe(uglifyES({ 
       mangle: false, 
       ecma: 5 
    }))
    .pipe(concat(paths.vendorScriptOutput))
    .pipe(sourcemaps.write({addComment: false}))
    /* output in same directory */
    .pipe(gulp.dest(function (file) {
        return file.base+'min/';
    }))
});

/* run on change */
gulp.task('watch', function() {
    gulp.watch('!WebContent/resources/css/eninetworkgraph*.css', ['vendorcss']);
    gulp.watch('!WebContent/resources/js/eninetworkgraph*.js', ['vendorjs']);
    gulp.watch('WebContent/resources/css/eninetworkgraph*.css', ['css']);
    gulp.watch('WebContent/resources/js/eninetworkgraph*.js', ['js']);
});

gulp.task('default', ['vendorcss', 'vendorjs', 'css', 'js', 'watch' ]);