module.exports = function(grunt) {

      grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        clean: ['target/dist'],
        copy: {
          main: {
            expand: true,
            cwd: 'src/main/webapp',
            src: '**',
            dest: 'target/dist',
          },
        },
        cacheBust: {
            taskName: {
                options: {
                    assets: ['js/app/**/*', 'css/*'],
                    baseDir: 'target/dist/',
                    outputDir: 'assets/',
                    clearOutputDir: true,
                    deleteOriginals: false,
                    queryString: false
                },
                files: [{
                  src: ['target/dist/index.html']
                }]
            }
        },
        ngtemplates:  {
          Rvd: {
            cwd:      'target/dist',
            src:      'templates/**/*.html',
            dest:     'target/dist/js/app/scriptedTemplates.js',
            options:    {
              append: true
            }
          }
        },
      });

      grunt.loadNpmTasks('grunt-angular-templates');
      grunt.loadNpmTasks('grunt-contrib-clean');
      grunt.loadNpmTasks('grunt-contrib-copy');
      grunt.loadNpmTasks('grunt-cache-bust');

      grunt.registerTask('default', ['clean', 'copy', 'ngtemplates', 'cacheBust']);

};

