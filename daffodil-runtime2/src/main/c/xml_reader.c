/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "xml_reader.h"
#include <errno.h>  // for errno, ERANGE
#include <limits.h> // for LONG_MAX, LONG_MIN
#include <mxml.h>   // for mxmlWalkNext, mxmlGetElement, mxmlGetType, ...
#include <stdint.h> // for int32_t, INT32_MAX, INT32_MIN
#include <stdlib.h> // for NULL, strtol
#include <string.h> // for strcmp, strerror

// Read XML data from stream before walking infoset

static const char *
xmlStartDocument(XMLReader *reader)
{
    reader->xml = mxmlLoadFile(NULL, reader->stream, MXML_OPAQUE_CALLBACK);
    reader->node = reader->xml;
    return reader->xml ? NULL : "Unable to read XML data from input file";
}

// Free XML data after walking infoset

static const char *
xmlEndDocument(XMLReader *reader)
{
    mxmlDelete(reader->xml);
    reader->xml = NULL;
    reader->node = NULL;
    return NULL;
}

// Check we're walking both XML data and infoset in lockstep

static const char *
xmlStartComplex(XMLReader *reader, const InfosetBase *base)
{
    do
    {
        reader->node = mxmlWalkNext(reader->node, reader->xml, MXML_DESCEND);
    } while (mxmlGetType(reader->node) == MXML_OPAQUE);

    const char *name_from_xml = mxmlGetElement(reader->node);
    const char *name_from_infoset = base->erd->namedQName.name;

    if (name_from_xml && name_from_infoset)
    {
        return strcmp(name_from_xml, name_from_infoset) == 0
                   ? NULL
                   : "Found mismatch between XML data and infoset";
    }
    else
    {
        return "Ran out of XML data";
    }
}

// Walk XML data only on start events, not end events

static const char *
xmlEndComplex(XMLReader *reader, const InfosetBase *base)
{
    (void)reader;
    (void)base;
    return NULL;
}

// Walk XML data in lockstep and read 32-bit integer value

static const char *
xmlInt32Elem(XMLReader *reader, const ERD *erd, int32_t *location)
{
    do
    {
        reader->node = mxmlWalkNext(reader->node, reader->xml, MXML_DESCEND);
    } while (mxmlGetType(reader->node) == MXML_OPAQUE);

    const char *name_from_xml = mxmlGetElement(reader->node);
    const char *name_from_infoset = erd->namedQName.name;

    if (name_from_xml && name_from_infoset)
    {
        if (strcmp(name_from_xml, name_from_infoset) == 0)
        {
            const char *number = mxmlGetOpaque(reader->node);
            char *      endptr = NULL;
            errno = 0; // To distinguish success/failure after call
            const long int value = strtol(number, &endptr, 10);
            // Check for various possible errors
            if ((errno == ERANGE && (value == LONG_MAX || value == LONG_MIN)) ||
                (errno != 0 && value == 0))
            {
                return strerror(errno);
            }
            else if (endptr == number)
            {
                return "No digits were found";
            }
            else if (*endptr != '\0')
            {
                return "Found further characters after number";
            }
            else if (value < INT32_MIN || value > INT32_MAX)
            {
                return "Number out of range for int32_t";
            }
            else
            {
                *location = (int32_t)value;
                return NULL;
            }
        }
        else
        {
            return "Found mismatch between XML data and infoset";
        }
    }
    else
    {
        return "Ran out of XML data";
    }
}

// Initialize a struct with our visitor event handler methods

const VisitEventHandler xmlReaderMethods = {
    (VisitStartDocument)&xmlStartDocument, (VisitEndDocument)&xmlEndDocument,
    (VisitStartComplex)&xmlStartComplex,   (VisitEndComplex)&xmlEndComplex,
    (VisitInt32Elem)&xmlInt32Elem,
};
