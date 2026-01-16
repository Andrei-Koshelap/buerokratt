keytool -genkeypair ^
 -alias sp-signing ^
 -keyalg RSA ^
 -keysize 2048 ^
 -sigalg SHA256withRSA ^
 -validity 3650 ^
 -dname "CN=saml-sp, OU=Dev, O=Demo, L=Tallinn, C=EE" ^
 -storetype PKCS12 ^
 -keystore src/main/resources/sp-keystore.p12 ^
 -storepass changeit ^
 -keypass changeit


Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 3,650 days
for: CN=saml-sp, OU=Dev, O=Demo, L=Tallinn, C=EE

keytool -exportcert -alias sp-signing -keystore src/main/resources/sp-keystore.p12 -storepass changeit -rfc -file sp-signing.crt

type sp-signing.crt
-----BEGIN CERTIFICATE-----
MIIDPzCCAiegAwIBAgIIaTJN7GJIM7swDQYJKoZIhvcNAQELBQAwTjELMAkGA1UE
BhMCRUUxEDAOBgNVBAcTB1RhbGxpbm4xDTALBgNVBAoTBERlbW8xDDAKBgNVBAsT
A0RldjEQMA4GA1UEAxMHc2FtbC1zcDAeFw0yNjAxMTYwODMyMzFaFw0zNjAxMTQw
ODMyMzFaME4xCzAJBgNVBAYTAkVFMRAwDgYDVQQHEwdUYWxsaW5uMQ0wCwYDVQQK
EwREZW1vMQwwCgYDVQQLEwNEZXYxEDAOBgNVBAMTB3NhbWwtc3AwggEiMA0GCSqG
SIb3DQEBAQUAA4IBDwAwggEKAoIBAQCreeuTH3okhxPk81Sv0BlkiAjxFztMbXoI
oHyAcxm6E7y5ZooPOHC6nYTIhtAVGgqRhQHa2DgQJ41xqEGNyzayN0IIzVoomChk
FRmH4LGqqb4uS5+DMFxhNcLE0gvMGOILzBKezeXbBAX2Uuo4LmTipFoQ4t3/cdBy
FvjKY4k3QT3voxgsbkl6sECx+h3RIPFlI+uv9rXGIZCR5G1udXB0kYuFx2Dpn+Ah
/dLj2SMcO+Gy9Lv+wysykb2SQIJ3rH4BFpQWjsDLlUomX/oFa3X4ElKpCQ5amvZS
PDwhWdBzkn2Sksfyx9a4IEjOjGjHJZeyw2Lprud7ywAYNUy/LkMnAgMBAAGjITAf
MB0GA1UdDgQWBBRsjeY6tFmjM3ENHPs6O6a0nkQrbTANBgkqhkiG9w0BAQsFAAOC
AQEAPKk1K81mZMvREV4vQ7SL+cScU+/eIOK/eXIlo9S00oWL6wryUdsvIqpG0vMp
wOtY8RJ+K9JbIxPIdngz/iRlg4vWlxLK5wy+3zbMy6GfWMHqR89uX0FJOIy7bJlO
8473IvrHKy6jKhSzD5k6+Oqv0BuXrTIrFCk13rS6OGu1uUcnGPQL47myADoKhCWP
6uKnJbgVTD7Ec3IIxs/BGes7reRPSE9AJUdnqNB0rnuKzxJo8J/Z6WXwzEYmEkne
SYUxWojaGvPMGnEEp204ApomrkretdZmmDZoI68/vc+QKC3gaK9Jsmd4JIpHEPDc
9afxu/OPY7MYVkijT/IowPl5PA==
-----END CERTIFICATE-----

Client → Keys / Signature



ngrok http 8080
this gives a public URL like https://abcd1234.ngrok.io which tunnels to your localhost:8080

Identifier (Entity ID)
urn:sp:saml-sp
Reply URL (Assertion Consumer Service URL)
https://eastbound-christine-overhurried.ngrok-free.dev/login/saml2/sso/entra