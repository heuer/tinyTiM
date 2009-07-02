====================================
tinyTiM - The tiny Topic Maps engine
====================================

What is tinyTiM MIO?
--------------------
tinyTiM is a tiny Topic Maps engine with a small jar footprint.
This Topic Maps engine is meant to be used together with the TMAPI interfaces,
see <http://www.tmapi.org/> for details.

The MIO package provides an API to import serialized topic maps. It depends
on Semagia MIO which provides a streaming Topic Maps API implementation.

Note: Semagia MIO uses another license than tinyTiM, see 
``LICENSE.semagia-mio.txt`` for details.



Installation
------------
Put the ``tinytim-mio-<VERSION>.jar`` together with the ``tinytim-<VERSION>.jar``,
and ``tmapi-<VERSION>.jar`` into the classpath.
Further, the ``semagia-mio-<VERSION>.jar`` and one or more 
``semagia-mio-<SYNTAX-NAME>-<VERSION>.jar`` are needed in the classpath.
Note, that ``Semagia MIO`` needs the ``Simple Logging Facade for Java`` 
(SLF4J), so ``slf4j-api-<VERSION>.jar`` and one of the 
``slf4j-<TYPE>-<VERSION>.jar`` from the SLF4J project must be in the classpath; 
see <http://www.slf4j.org/> for details. The distribution comes with a logger
which uses the JDK 1.4 ``java.util.logging`` implementation.


Latest Version
--------------
Visit tinyTiM's homepage <http://tinytim.sourceforge.net/> for the
latest version.


Mailing list
------------
The mailing list for tinyTiM is located at 
<http://tinytim.sourceforge.net/mailinglist>.
Feel free to ask any question about tinyTiM. :)


License
-------
tinyTiM is licensed under the Apache License Version 2.0, see LICENSE.txt 
for details.
