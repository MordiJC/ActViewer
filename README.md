# ActViewer

#### Description
This application is tool for parsing Polish Acts and Constitution.

#### Command Line format
```
java -jar act-viewer-<version>.jar [-ht] [-a=<article path>] [-c=<chapter>] [-r=<article>] [-s=<section>] FILE
```

#### Opitons
| Option | Description | Note |
| ------ | ----------- | ---- |
| \-h<br>\-\-help | Display help and exit. | |
| \-t<br>\-\-toc | Display table of contents| |
| \-a<br>\-\-article=\<article path\> | Display content of article or child element of article under path.<br>Path should be string with identificators separated by dot.<br>Example: For article `7`, paragraph `3`, letter `a`:<br> `--article=7.3.a`| |
| \-r<br>\-\-range=\<article\> | Display range of articles. | \-a or \-\-article required. |
| \-c<br>\-\-chapter=\<chapter\> | Display chapter. If there is more than one chapter in whole act then only first one woll be displayed. | |
| \-s<br>\-\-section=<section> | Display section. It here is more than one section then first one will be displayed. | |

#### Compilation
##### Requirements
|Application|Version|
|-----------|-------|
|JDK|1.9|
|Gradle|>=4.4.1|

##### Steps with command line
1. Go to project main directory.
2. Execute `gradlew clean && gradlew build`
3. Built application will be in `<build-directory>/libs/`

##### Steps with IntelliJ
1. Open project in IntelliJ or import it as Gradle project.
2. Build
3. Run or execute jar from `<build-directory>/libs/`


#### Used libraries
##### - PicoCLI
