javac --source-path ./ --module-path ./lib/ --add-modules javafx.controls ./GameApp.java

java --class-path ./ --module-path ./lib/ --add-modules=javafx.controls GameApp
