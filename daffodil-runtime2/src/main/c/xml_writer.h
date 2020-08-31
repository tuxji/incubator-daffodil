#ifndef XML_WRITER_H
#define XML_WRITER_H

#include "common_runtime.h" // for VisitEventHandler
#include <stdio.h>          // for FILE

// XMLWriter - infoset visitor with methods to output XML

typedef struct XMLWriter
{
    const VisitEventHandler handler;
    FILE *                  stream;
} XMLWriter;

// XMLWriter methods to pass to walkInfoset method

extern const VisitEventHandler xmlWriterMethods;

#endif // XML_WRITER_H
