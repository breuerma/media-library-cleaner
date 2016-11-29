# Media Library Cleaner

This project started as demo project for showing how to do some stuff with
Spring Boot. It has proven quite useful to me so I decided to push it here.

## What it does

It does scan a given directory and record all MD5 sums for a given list of
suffixes. You may then access a tiny (and very ugly) web frontend under
http://yourhost.domain:8080/ and decide whether you would like to remove the
files which share the same MD5 sum.
The use is merely to find duplicate media files in huge libraries and being
able to remove the unwantend ones easily.

## Usage

First you should create a postgres database which is accessible from your host.
The database user must have the right to create tables and sequences because
the schema setup will be done by the application.

Then edit the file src/main/resources/application.properties according to
your needs (see below)

Now create a jar with 

`mvn package`

Then you may run that jar just from the command line.

`java -jar target/mediaregistry`

If you would like to start a scan immediately then use

`java -jar target/mediaregistry --start-initial-scan`

and lean back as it will take some time (15h on my 6T repo with 15k files).

After the scan has been finished you may see all files which are very very
likely duplicates under:

http://yourhost.domain:8080/

The scan will be repeated according to the settings in the
application.properties.

## Settings:

In application properties the following variables can be edited:

	spring.datasource.url=jdbc:postgresql://dbhost.local/moviescan
	spring.datasource.username=moviescan
	spring.datasource.password=moviescan

	logging.file=mediaregistry.log

	flyway.enabled=true
	flyway.check-location=true
	flyway.locations=classpath:/db

	# Your media directory:
	mediaregistry.directory=/home/media/
	# The scan depth - that is the number of directories stacked into each other:
	mediaregistry.max.scanner.depth=10
	# List of media file suffixes to include in scanning:
	mediaregistry.suffixList=.mpg,.mpeg,.mp4, .m4v, .mkv, .vob, .avi, .asf, .wmv, .divx, .swf, .flv, .rm, .rmvb, .mov, .ogg, .3gp, .iso, .mp3, .wma, .flac, .wav
	# Be careful: Setting threads to n will start n md5sum processes on your system.
	# As md5sum is heavily reading from your harddisk or SDD you should not set the value too high: 
	mediaregistry.threads=2
	# The path of the md5sum command:
	mediaregistry.md5sum.command=/usr/bin/md5sum
	# Use binary scanning:
	mediaregistry.md5sum.parameters=-b
	# The path of the rm command:
	mediaregistry.rm.command=/bin/rm

