Só criei para guardar este link por enq :P
https://www.geeksforgeeks.org/computer-networks/elgamal-encryption-algorithm/

Ja funfa o:
mvn clean install 


Steps to conf My-SQL
sudo mysql

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'pwdOSEOMDM324MCNMSKHCJCLCMDJ';

FLUSH PRIVILEGES;

EXIT;

For some reason if u want to rejoin:
mysql -u root -p
pwdOSEOMDM324MCNMSKHCJCLCMDJ


funfante:
mvn exec:java@server

keytool -genkeypair \
  -alias serversigned \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore serverstore.p12 \
  -storetype PKCS12 \
  -storepass UPSTGQEDQAPVCASMLAppidmfP1331928 \
  -keypass UPSTGQEDQAPVCASMLAppidmfP1331928 \
  -dname "CN=localhost, OU=LEI, O=Universidade, L=Lisboa, C=PT"