# SubFinder

Small command line tool to get subtitles from https://feliratok.info

## Purpose
This tool is designed to download proper subtitle files from [Feliratok.info](https://feliratok.info) (a hungarian site for subtitles for movies and series). The tool is taking responsibility of the proper release, so if there is no subtitle
for the specific release, it will not download anything. If it finds the proper subtitle, it will download it and place it next to the video
file with the same format like the original video file has. Movies, and other files will be ignored.

## Prerequisites
1. Install and configure at least Java version 12. OpenJDK and Oracle all works.

If the configuration was successful, execute the following command to make sure you are the proper version:

```bash
burgatshow@chappie [~/Temp]$ java --version
openjdk 14 2020-03-17
OpenJDK Runtime Environment (build 14+36-1461)
OpenJDK 64-Bit Server VM (build 14+36-1461, mixed mode, sharing)
```

## Installation
1. Download the most recent JAR file [from releases](https://github.com/burgatshow/subfinder/releases).
2. Extract it to your favorite place.

## Configuration

See sample `SubFinderConfig.properties` for reference.

1. Create an empty text file, ending with `.properties` extension.
2. Populate the file with your series (one series per line).
3. Use `UPPERCASE` characters for the series name **(replace all special characters to underscore)**, followed by a `=` (equal) and the series ID from the site.
4. Save the file and put wherever you want.

Example:

```
	THE_BLACKLIST=1111
	STAR_WARS_THE_CLONE_WARS=2222
```

The series ID number can be located in the URL param called `sid` when searching for a specific series, for example:

```
https://www.feliratok.info/index.php? \ 
	search= \
	&soriSorszam= \
	&nyelv= \
	&sorozatnev=This+Is+Us+%282016%29 \
	&sid=4138 \  <----------------------------- THIS ONE
	&complexsearch=true \
	&knyelv=0 \
	&evad= \
	&epizod1= \
	&elotag=0 \
	&minoseg=0 \
	&rlsr=0 \
	&tab=all
```

## Usage
`java -jar SubFinder.jar --folder path --mapping path [ -VTH ]`

The program accepts the following arguments:

- `-F, --folder` **(*)** The folder where the tool can scan the series. Obviously this is the folder where your files are downloaded.
- `-M, --mapping` **(*)** The path where from the tool can load the special properties file which contains `series=id` mappings. Required because of the structure of the site where we are downloading from.
- `-E, --video-sizelimit` The minimum file size in `MB` to consider a valid video file. If not set, `300 MB` will be used.
- `-S, --subtitle-sizelimit` The minimum file size in `KB` to consider valid subtitle file. If not set, `5 KB` will be used.
- `-V, --verbose` Provides verbose, detailed output when running.
- `-T, --test` Test run (or dry run) with the given configuration. It will simulate what will happen, but it does not download and write anything to the disk.
- `-H, --help` Displays this help and exits.Keep in mind, that until this argument is present, the tool will not do anything!

Arguments marked with **(*)** are required.

Example command line:

```bash
java -jar /Users/burgatshow/SubFinder/SubFinder.jar \ 
	--folder /Users/burgatshow/SubFinder/videos \
	--mapping /Users/burgatshow/SubFinder/SubFinderConfig.properties \
	--video-sizelimit 1000 \
	--subtitle-sizelimit 10 \
	--verbose
```

The command above will start the application located in `/Users/burgatshow/SubFinder/SubFinder.jar`, maps the working video folder (`--folder`) to `/Users/burgatshow/SubFinder/videos`. It also maps the configuration file (`--mapping`) to `/Users/burgatshow/SubFinder/SubFinderConfig.properties`. Finally, it enables verbose mode (`--verbose`) and sets the file limit for video (`--video-sizelimit`) to `1000 MB` and for subtitle (`--subtitle-sizelimit`) to `10 KB`.

## Troubleshooting
Use the verbose mode if you think something is wrong. Check the configuration file as well.
