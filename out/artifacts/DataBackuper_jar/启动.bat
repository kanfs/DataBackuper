@echo off                                                                                                                                                 
%1 mshta vbscript:CreateObject("WScript.Shell").Run("%~s0 ::",0,FALSE)(window.close)&&exit
java -jar .\DataBackuper.jar > .\log.txt 2>&1 &
exit