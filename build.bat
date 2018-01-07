
javac -sourcepath ./src ./src/demo/hdz/pcctrlandroid/Logger.java -d ./bin  -encoding utf-8

javac -sourcepath ./src ./src/demo/hdz/pcctrlandroid/pc/Client.java -d ./bin  -encoding utf-8

javac -sourcepath ./src ./src/demo/hdz/pcctrlandroid/pc/Install.java -d ./bin  -encoding utf-8


javac -bootclasspath ./libs/android-22/android.jar -sourcepath ./src -source 1.7 -target 1.7 ./src/demo/hdz/pcctrlandroid/android/Main.java -d ./bin 


cd bin
dx --dex --output=Main.dex ./demo/hdz/pcctrlandroid/android/*.class


 
