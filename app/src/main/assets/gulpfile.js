var gulp = require('gulp');
var gutil = require('gulp-util');
var source = require('vinyl-source-stream');
var watchify = require('watchify');
var browserify = require('browserify');
var uglify = require('gulp-uglify');
var buffer = require('gulp-buffer');

gulp.task('watch', function() {
  var bundler = watchify(browserify('./src/book.coffee', watchify.args));

  // Optionally, you can apply transforms
  // and other configuration options on the
  // bundler just as you would with browserify
  bundler.transform('coffeeify');

  bundler.on('update', rebundle);

  function rebundle() {
    return bundler.bundle()
      // log errors if they happen
      .on('error', gutil.log.bind(gutil, 'Browserify Error'))
      .pipe(source('book-bundle.js'))
	  .pipe(buffer())
	  .pipe(uglify())
      .pipe(gulp.dest('./dist'));
  }

  return rebundle();
});
