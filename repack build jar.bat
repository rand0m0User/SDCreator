@echo off
color 0a
"C:\Program Files\7-Zip\7z" a -sdel tmp.null build
del tmp.null
cd dist
..\..\..\__NBP_tools_dir__\advzip.exe -z -3 SDCreator.jar
del README.TXT
cd..