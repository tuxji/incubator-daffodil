#ifndef COMMON_RUNTIME_H
#define COMMON_RUNTIME_H

#include <stddef.h> // for ptrdiff_t
#include <stdint.h> // for int32_t
#include <stdio.h>  // for FILE, size_t

// Prototypes needed for compilation

typedef struct ElementRuntimeData ERD;
typedef struct InfosetBase        InfosetBase;
typedef struct PState             PState;
typedef struct UState             UState;
typedef struct VisitEventHandler  VisitEventHandler;

typedef void (*ERDInitSelf)(InfosetBase *infoNode);
typedef const char *(*ERDParseSelf)(InfosetBase * infoNode,
                                    const PState *pstate);
typedef const char *(*ERDUnparseSelf)(const InfosetBase *infoNode,
                                      const UState *     ustate);

typedef const char *(*VisitStartDocument)(const VisitEventHandler *handler);
typedef const char *(*VisitEndDocument)(const VisitEventHandler *handler);
typedef const char *(*VisitStartComplex)(const VisitEventHandler *handler,
                                         const InfosetBase *      base);
typedef const char *(*VisitEndComplex)(const VisitEventHandler *handler,
                                       const InfosetBase *      base);
typedef const char *(*VisitInt32Elem)(const VisitEventHandler *handler,
                                      const ERD *erd, const int32_t *location);

// NamedQName - name of an infoset element

typedef struct NamedQName
{
    char *name;  // element name (including prefix if any)
    char *xmlns; // xmlns attribute name (including prefix if any)
    char *ns;    // xmlns attribute value (a namespace URI)
} NamedQName;

// TypeCode - type of an infoset element

enum TypeCode
{
    COMPLEX,
    PRIMITIVE_INT32
};

// ERD - element runtime data needed to parse/unparse objects

typedef struct ElementRuntimeData
{
    const NamedQName    namedQName;
    const enum TypeCode typeCode;
    const size_t        numChildren;
    const ptrdiff_t *   offsets;
    const ERD **        childrenERDs;

    const ERDInitSelf    initSelf;
    const ERDParseSelf   parseSelf;
    const ERDUnparseSelf unparseSelf;
} ERD;

// InfosetBase - representation of an infoset element

typedef struct InfosetBase
{
    const ERD *erd;
} InfosetBase;

// PState - parser state while parsing input

typedef struct PState
{
    FILE *stream; // input to read from
} PState;

// UState - unparser state while unparsing infoset

typedef struct UState
{
    FILE *stream; // output to write to
} UState;

// VisitEventHandler - methods to be called when walking an infoset

typedef struct VisitEventHandler
{
    const VisitStartDocument visitStartDocument;
    const VisitEndDocument   visitEndDocument;
    const VisitStartComplex  visitStartComplex;
    const VisitEndComplex    visitEndComplex;
    const VisitInt32Elem     visitInt32Elem;
} VisitEventHandler;

// walkInfoset - walk an infoset and call VisitEventHandler methods

extern const char *walkInfoset(const VisitEventHandler *handler,
                               const InfosetBase *      infoset);

#endif // COMMON_RUNTIME_H
