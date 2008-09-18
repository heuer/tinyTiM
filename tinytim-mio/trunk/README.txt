====================================
tinyTiM - The tiny Topic Maps engine
====================================

What is tinyTiM I/O?
--------------------
tinyTiM is a tiny Topic Maps engine which keeps topic maps in-memory.
This Topic Maps engine is meant to be used together with the TMAPI interfaces,
see <http://www.tmapi.org/> for details.

The I/O package provides an API to import serialized topic maps. It depends
on Semagia MIO which provides a streaming Topic Maps API implementation.

Note: Semagia MIO uses another license than tinyTiM, see 
``LICENSE.semagia-mio.txt`` for details.



Installation
------------
Put the ``tinytim-io-<VERSION>.jar`` together with the ``tinytim-<VERSION>.jar``,
and ``tmapi-1_0SP1.jar`` into the classpath.
Further, the ``semagia-mio-<VERSION>.jar`` and one or more 
``semagia-mio-<SYNTAX-NAME>-<VERSION>.jar`` are needed in the classpath.
The standard tinyTiM I/O distribution offers the ``semagia-mio-xtm-<VERSION>.jar``
which is used to import XML Topic Maps (XTM) version 1.0 and 2.0.
Note, that ``Semagia MIO`` needs the ``Simple Logging Facade for Java`` 
(SLF4J), so ``slf4j-api-<VERSION>.jar`` and one of the 
``slf4j-<TYPE>-<VERSION>.jar`` from the SLF4J project must be in the classpath; 
see <http://www.slf4j.org/> for details. The distribution comes with a logger
which uses the JDK 1.4 ``java.util.logging`` implementation.


Latest Version
--------------
Visit tinyTiM's homepage <http://sourceforge.net/projects/tinytim> for the
latest version.


Mailing list
------------
The mailing list for tinyTiM is located here 
<http://sourceforge.net/mailarchive/forum.php?forum_name=tinytim-discuss>.
Feel free to ask any question about tinyTiM. :)


License
-------
tinyTiM is licensed under the Apache License Version 2.0, see LICENSE.txt 
for details.
