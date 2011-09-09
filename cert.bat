REM Copyright 2008 Sahab Yazdani
REM 
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM 
REM    http://www.apache.org/licenses/LICENSE-2.0
REM 
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.


REM generate a phony self-signed certificate

REM -keyalg DSA for JDK 1.2 compatibility, private-public pair.
REM However RSA is what most modern certs use.
REM see http://docs.sun.com/source/816-5539-10/app_dn.htm for construction of distinguished name
REM create private/public key pair
keytool -genkey -storepass bobjoemagee -keyalg DSA -alias saliencescert2008dsa -dname "CN=saliences.com, OU=Java Code, O=saliences.com, L=Toronto, ST=Ontario, C=CA, EMAILADDRESS=admin@saliences.com DC=saliences, DC=com" -validity 999

REM generate the self-signed certificate containing public key
keytool -selfcert  -storepass bobjoemagee -alias saliencescert2008dsa  -validity 999

REM export the self-signed certificate in x.509 printable format, public key only.
REM Prior to Java 1.6 use -export instead of -exportcert
keytool -exportcert  -storepass bobjoemagee -alias saliencescert2008dsa -rfc -file saliencescert2008dsa.cer