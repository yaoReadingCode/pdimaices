start /wait /b java -cp ./bd/hsqldb.jar org.hsqldb.Server -database.0 bd/maices -dbname.0 xdb
start /wait /b java -jar pdimaices.jar -Xmx512m -Xms40m