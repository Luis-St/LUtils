# FTP RFC 959 + RFC 4217 (+ Common Extensions) TL;DR

FTP (File Transfer Protocol) uses two TCP connections:

1. **Control Connection**
    - Usually TCP/21
    - Carries commands and responses
    - Remains open throughout the session

2. **Data Connection**
    - Separate connection used for file transfers and directory listings
    - Created for each transfer/listing operation

FTP supports:

- Authentication (`USER`, `PASS`, etc.)
- Directory navigation (`CWD`, `PWD`, etc.)
- File upload/download (`STOR`, `RETR`, etc.)
- Transfer parameter negotiation (`TYPE`, `MODE`, `STRU`)
- Active mode (`PORT`)
- Passive mode (`PASV`)
- TLS security extensions (RFC 4217)

## Active vs Passive

### Active Mode

Client opens control connection and tells server where to connect for data transfer.

```
PORT h1,h2,h3,h4,p1,p2
```

Server initiates the data connection.

Commonly blocked by firewalls/NAT.

### Passive Mode

Client asks server for a listening port.

```
PASV
```

Server responds with host/port and client initiates the data connection.

Preferred today.

### Extended Passive Mode

```
EPSV
```

Works with IPv4 and IPv6 and is preferred over PASV.

## FTPS (RFC 4217)

FTP over TLS.

Typical sequence:

```
AUTH TLS
234

< TLS Handshake >

PBSZ 0
200

PROT P
200

USER myuser
PASS mypassword
```

Control channel becomes encrypted.
`PROT P` additionally encrypts data channels.

---

# Common FTP Status Codes

These may occur for most commands.

```
1xx  Positive Preliminary Reply
110  Restart marker reply
120  Service ready in N minutes
125  Data connection already open
150  File status okay; opening data connection

2xx  Positive Completion Reply
200  Command okay
202  Command not implemented, superfluous
211  System status
212  Directory status
213  File status
214  Help message
215  System type
220  Service ready
221  Service closing control connection
225  Data connection open
226  Closing data connection; transfer successful
227  Entering Passive Mode
229  Entering Extended Passive Mode
230  User logged in
234  AUTH command accepted (TLS)

3xx  Positive Intermediate Reply
331  Username okay; need password
332  Need account for login
350  Requested file action pending further information

4xx  Transient Negative Reply
421  Service not available
425  Cannot open data connection
426  Connection closed; transfer aborted
450  Requested file action not taken
451  Local error
452  Insufficient storage

5xx  Permanent Negative Reply
500  Syntax error
501  Bad parameter syntax
502  Command not implemented
503  Bad sequence of commands
504  Command not implemented for parameter
530  Not logged in
532  Need account for storing files
550  Requested action not taken
551  Page type unknown
552  Exceeded storage allocation
553  Invalid file name
```

---

# RFC 959 Core Commands

# USER

Specify username.

```
USER <username>

USER anonymous

331
230
530
```

# PASS

Specify password.

```
PASS <password>

PASS secret

230
202
332
530
```

# ACCT

Provide account information when required.

```
ACCT <account>

ACCT billing01

230
202
332
530
```

# CWD

Change working directory.

```
CWD <directory>

CWD /pub/releases

200
250
550
```

# CDUP

Move to parent directory.

```
CDUP

CDUP

200
250
550
```

# SMNT

Mount a different filesystem structure.

Rarely implemented today.

```
SMNT <pathname>

SMNT /mounted_fs

202
250
500
502
550
```

# QUIT

Terminate session.

```
QUIT

QUIT

221
```

# REIN

Reinitialize session without closing connection.

```
REIN

REIN

120
220
421
```

# PORT

Active mode data connection information.

```
PORT h1,h2,h3,h4,p1,p2

PORT 192,168,1,10,195,80

200
500
501
425
```

# PASV

Enter passive mode.

```
PASV

PASV

227
425
500
502
```

# TYPE

Set file representation type.

### Common values

```
TYPE A
```

ASCII text mode.

```
TYPE I
```

Image/Binary mode (most common today).

```
TYPE E
```

EBCDIC.

```
TYPE L 8
```

Local byte size.

Always use `TYPE I` for binaries, archives, PDFs, images, executables, etc.

```
TYPE <type>

TYPE I

200
500
501
504
```

# STRU

Set file structure.

### Values

```
STRU F
```

File structure (almost always used).

```
STRU R
```

Record structure.

```
STRU P
```

Page structure.

Modern FTP servers generally support only:

```
STRU F
```

```
STRU <structure>

STRU F

200
504
500
501
```

# MODE

Set transfer mode.

### Values

```
MODE S
```

Stream mode (modern default).

```
MODE B
```

Block mode.

```
MODE C
```

Compressed mode.

Nearly all modern servers only support:

```
MODE S
```

```
MODE <mode>

MODE S

200
504
500
501
```

# RETR

Download file.

```
RETR <filename>

RETR report.pdf

125
150
226
425
426
550
```

# STOR

Upload file.

```
STOR <filename>

STOR upload.zip

125
150
226
425
426
450
550
```

# STOU

Store unique.

Server generates a unique filename.

Useful when client wants to upload without risking overwriting an existing file.

Example server may create:

```
upload.1
upload.2
upload.3
```

Server returns generated name.

```
STOU

STOU

125
150
226
250
425
426
450
550
553
```

# APPE

Append data to existing file.

```
APPE <filename>

APPE logfile.txt

125
150
226
425
550
```

# ALLO

Allocate storage before upload.

Mostly ignored today.

```
ALLO <bytes>

ALLO 1048576

200
202
500
501
504
```

# REST

Restart transfer at specified offset.

Used for resume support.

```
REST <offset>

REST 1048576

350
500
501
```

# RNFR

Rename from.

First step of rename operation.

```
RNFR <oldname>

RNFR file1.txt

350
450
550
```

# RNTO

Rename to.

Second step after RNFR.

```
RNTO <newname>

RNTO file2.txt

250
503
550
```

# ABOR

Abort transfer.

```
ABOR

ABOR

225
226
426
```

# DELE

Delete file.

```
DELE <filename>

DELE old.txt

250
450
550
```

# RMD

Remove directory.

```
RMD <directory>

RMD olddir

250
550
```

# MKD

Create directory.

```
MKD <directory>

MKD uploads

257
550
```

# PWD

Print working directory.

```
PWD

PWD

257
```

# LIST

Human-readable directory listing.

```
LIST [path]

LIST /pub

125
150
226
450
550
```

# NLST

Name list only.

```
NLST [path]

NLST

125
150
226
450
550
```

# SITE

Site-specific extensions.

```
SITE <subcommand>

SITE HELP

200
202
500
502
```

# SYST

Return server operating system type.

```
SYST

SYST

215
```

# STAT

Return status information.

```
STAT [path]

STAT

211
212
213
450
```

# HELP

Help information.

```
HELP [command]

HELP RETR

211
214
```

# NOOP

No operation. Common keepalive command.

```
NOOP

NOOP

200
```

---

# RFC 2228 / RFC 4217 Security Commands

# AUTH

Negotiate security mechanism.

For FTPS:

```
AUTH TLS
```

```
AUTH <mechanism>

AUTH TLS

234
334
431
500
502
504
534
```

# ADAT

Authentication data exchange.

Mainly used by non-TLS security mechanisms.

```
ADAT <data>

ADAT base64blob

235
335
431
501
503
```

# PBSZ

Protection buffer size.

RFC 4217 requires:

```
PBSZ 0
```

before `PROT`.

```
PBSZ 0

PBSZ 0

200
501
503
536
```

# PROT

Defines data channel protection.

Values:

```
PROT C
```

Clear data channel.

```
PROT P
```

Private (encrypted) data channel.

For FTPS use:

```
PROT P
```

```
PROT <level>

PROT P

200
431
501
503
536
```

# CCC

Clear command channel.

Drops TLS protection on control connection.

Rarely used.

```
CCC

CCC

200
533
534
```

# MIC

Integrity protected command.

```
MIC <data>

MIC base64blob

200
500
503
533
```

# CONF

Confidentiality protected command.

```
CONF <data>

CONF base64blob

200
500
503
533
```

# ENC

Privacy protected command.

```
ENC <data>

ENC base64blob

200
500
503
533
```

---

# RFC 2389 Feature Negotiation

# FEAT

Return supported server extensions.

```
FEAT

FEAT

211
500
502
```

# OPTS

Supply options for an extension.

Frequently:

```
OPTS UTF8 ON
```

```
OPTS <feature> <options>

OPTS UTF8 ON

200
451
501
502
```

---

# RFC 2428 IPv6 / NAT Extensions

# EPRT

Extended PORT command.

Supports IPv4 and IPv6.

```
EPRT |<af>|<address>|<port>|

EPRT |2|2001:db8::1|60000|

200
500
501
522
```

# EPSV

Extended passive mode.

Preferred over PASV.

```
EPSV

EPSV

229
500
502
522
```

---

# RFC 3659 File Information Extensions

# MDTM

Get modification timestamp.

```
MDTM <file>

MDTM report.pdf

213
450
550
```

# SIZE

Get file size.

```
SIZE <file>

SIZE archive.zip

213
450
550
```

# MLST

Machine-readable metadata for one path.

```
MLST <path>

MLST report.pdf

250
501
550
```

# MLSD

Machine-readable directory listing.

Preferred over LIST for automation.

```
MLSD [path]

MLSD

125
150
226
450
550
```

---

# RFC 2640 Internationalization

# LANG

Select language.

```
LANG <tag>

LANG en-US

200
500
501
502
504
```

---

# RFC 7151 Virtual Hosting

# HOST

Select virtual host before login.

Useful when one FTP server serves multiple domains.

```
HOST <hostname>

HOST ftp.example.com

220
500
501
503
504
530
```

---

# Modern Recommended Client Login Sequence (FTPS)

```
CONNECT tcp/21

AUTH TLS
234

<TLS Handshake>

PBSZ 0
200

PROT P
200

USER myuser
331

PASS secret
230

FEAT
211

TYPE I
200

EPSV
229

MLSD
150
226

RETR file.zip
150
226

QUIT
221
```

Sources:
RFC 959 (File Transfer Protocol), RFC 2228 (FTP Security Extensions), RFC 2389 (FEAT/OPTS), RFC 2428 (EPRT/EPSV), RFC 2640 (LANG), RFC 3659 (MLST/MLSD/SIZE/MDTM), RFC 4217 (FTPS), RFC 7151 (HOST). 【1-bfa13f】【2-9a25fe】【3-2087f3】【4-4d09a9】