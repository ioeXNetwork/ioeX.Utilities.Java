#  ioeX.Utilities.Java

 -  ioeX.Utilities.Java is a tool for generating transaction signature,there are two forms of signature,one is to call the API，the other one is the web request

## Interface documentation

 - Path:[ioeX.Utilities.Java/src/main/java/docs](https://github.com/ioeXNetwork/ioeX.Utilities.Java/tree/master/src/main/java/docs)

## JAR package 
 
  - start command : java -cp ioeX.Utilities.Java.jar  org.ioexnetwork.ioexweb.HttpServer
  - suggestion：java version "1.8.0_161"
  - local IP : http://127.0.0.1:8989/
 
### generate jar package

 ```
File -> Project Structure -> Artifacts -> + -> JAR -> From modules with 
1、-> Main Class
2、-> extract to the target JAR
3、-> META-INF PATH （C:\DNA\src\ioex_tool\src\main\resources）
4、ok ->  Include in project build -> Apply ->ok

run : java -cp ioeX.Utilities.Java.jar  org.ioexnetwork.ioexweb.HttpServer
 ```

## notice
```
 - Execute the following command in the JAR directory to avoid invalid signature file exception information.
 - Execute ： zip -d ioeX.Utilities.Java.jar 'META-INF/*.SF' 'META-INF/*.RSA' 'META-INF/*SF'
```
