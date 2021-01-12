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

#define __STDC_WANT_IEC_60559_BFP_EXT__
#include "ex_nums.h"        // for generated code structs
#include "parsers.h"        // for parse_be_double, parse_be_float, ...
#include "unparsers.h"      // for unparse_be_double, unparse_be_float, ...
#include <math.h>           // for SNAN, SNANF
#include <stddef.h>         // for NULL, ptrdiff_t

// Prototypes needed for compilation

static void ex_nums_initSelf(ex_nums *instance);
static void ex_nums_parseSelf(ex_nums *instance, PState *pstate);
static void ex_nums_unparseSelf(const ex_nums *instance, UState *ustate);

// Metadata singletons

static const ERD be_double_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_double", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_DOUBLE, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_float_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_float", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_FLOAT, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_uint64_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_uint64", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT64, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_uint32_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_uint32", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT32, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_uint16_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_uint16", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT16, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_uint8_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_uint8", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT8, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_int64_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_int64", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT64, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_int32_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_int32", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT32, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_int16_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_int16", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT16, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD be_int8_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "be_int8", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT8, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_uint64_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_uint64", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT64, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_uint32_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_uint32", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT32, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_uint16_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_uint16", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT16, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_uint8_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_uint8", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_UINT8, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_int64_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_int64", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT64, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_int32_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_int32", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT32, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_int16_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_int16", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT16, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_int8_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_int8", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_INT8, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_float_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_float", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_FLOAT, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ERD le_double_ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "le_double", // namedQName.local
        NULL, // namedQName.ns
    },
    PRIMITIVE_DOUBLE, // typeCode
    0,               // numChildren
    NULL,            // offsets
    NULL,            // childrenERDs
    NULL,            // initSelf
    NULL,            // parseSelf
    NULL,            // unparseSelf
};

static const ex_nums ex_nums_compute_offsets;

static const ptrdiff_t ex_nums_offsets[20] = {
    (char *)&ex_nums_compute_offsets.be_double - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_float - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_uint64 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_uint32 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_uint16 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_uint8 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_int64 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_int32 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_int16 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.be_int8 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_uint64 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_uint32 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_uint16 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_uint8 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_int64 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_int32 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_int16 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_int8 - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_float - (char *)&ex_nums_compute_offsets,
    (char *)&ex_nums_compute_offsets.le_double - (char *)&ex_nums_compute_offsets
};

static const ERD *ex_nums_childrenERDs[20] = {
    &be_double_ex_nums__ERD,
    &be_float_ex_nums__ERD,
    &be_uint64_ex_nums__ERD,
    &be_uint32_ex_nums__ERD,
    &be_uint16_ex_nums__ERD,
    &be_uint8_ex_nums__ERD,
    &be_int64_ex_nums__ERD,
    &be_int32_ex_nums__ERD,
    &be_int16_ex_nums__ERD,
    &be_int8_ex_nums__ERD,
    &le_uint64_ex_nums__ERD,
    &le_uint32_ex_nums__ERD,
    &le_uint16_ex_nums__ERD,
    &le_uint8_ex_nums__ERD,
    &le_int64_ex_nums__ERD,
    &le_int32_ex_nums__ERD,
    &le_int16_ex_nums__ERD,
    &le_int8_ex_nums__ERD,
    &le_float_ex_nums__ERD,
    &le_double_ex_nums__ERD
};

static const ERD ex_nums__ERD = {
    {
        NULL, // namedQName.prefix
        "ex_nums", // namedQName.local
        "http://example.com", // namedQName.ns
    },
    COMPLEX,                         // typeCode
    20,                               // numChildren
    ex_nums_offsets,                      // offsets
    ex_nums_childrenERDs,                 // childrenERDs
    (ERDInitSelf)&ex_nums_initSelf,       // initSelf
    (ERDParseSelf)&ex_nums_parseSelf,     // parseSelf
    (ERDUnparseSelf)&ex_nums_unparseSelf, // unparseSelf
};

// Return a root element to be used for parsing or unparsing

InfosetBase *
rootElement()
{
    static ex_nums    instance;
    InfosetBase *root = &instance._base;
    ex_nums__ERD.initSelf(root);
    return root;
}

// Methods to initialize, parse, and unparse infoset nodes

static void
ex_nums_initSelf(ex_nums *instance)
{
    instance->be_double = SNAN;
    instance->be_float = SNANF;
    instance->be_uint64 = 0xCCCCCCCCCCCCCCCC;
    instance->be_uint32 = 0xCCCCCCCC;
    instance->be_uint16 = 0xCCCC;
    instance->be_uint8 = 0xCC;
    instance->be_int64 = 0xCCCCCCCCCCCCCCCC;
    instance->be_int32 = 0xCCCCCCCC;
    instance->be_int16 = 0xCCCC;
    instance->be_int8 = 0xCC;
    instance->le_uint64 = 0xCCCCCCCCCCCCCCCC;
    instance->le_uint32 = 0xCCCCCCCC;
    instance->le_uint16 = 0xCCCC;
    instance->le_uint8 = 0xCC;
    instance->le_int64 = 0xCCCCCCCCCCCCCCCC;
    instance->le_int32 = 0xCCCCCCCC;
    instance->le_int16 = 0xCCCC;
    instance->le_int8 = 0xCC;
    instance->le_float = SNANF;
    instance->le_double = SNAN;
    instance->_base.erd = &ex_nums__ERD;
}

static void
ex_nums_parseSelf(ex_nums *instance, PState *pstate)
{
    parse_be_double(&instance->be_double, pstate);
    parse_be_float(&instance->be_float, pstate);
    parse_be_uint64(&instance->be_uint64, pstate);
    parse_be_uint32(&instance->be_uint32, pstate);
    parse_be_uint16(&instance->be_uint16, pstate);
    parse_be_uint8(&instance->be_uint8, pstate);
    parse_be_int64(&instance->be_int64, pstate);
    parse_be_int32(&instance->be_int32, pstate);
    parse_be_int16(&instance->be_int16, pstate);
    parse_be_int8(&instance->be_int8, pstate);
    parse_le_uint64(&instance->le_uint64, pstate);
    parse_le_uint32(&instance->le_uint32, pstate);
    parse_le_uint16(&instance->le_uint16, pstate);
    parse_le_uint8(&instance->le_uint8, pstate);
    parse_le_int64(&instance->le_int64, pstate);
    parse_le_int32(&instance->le_int32, pstate);
    parse_le_int16(&instance->le_int16, pstate);
    parse_le_int8(&instance->le_int8, pstate);
    parse_le_float(&instance->le_float, pstate);
    parse_le_double(&instance->le_double, pstate);
}

static void
ex_nums_unparseSelf(const ex_nums *instance, UState *ustate)
{
    unparse_be_double(instance->be_double, ustate);
    unparse_be_float(instance->be_float, ustate);
    unparse_be_uint64(instance->be_uint64, ustate);
    unparse_be_uint32(instance->be_uint32, ustate);
    unparse_be_uint16(instance->be_uint16, ustate);
    unparse_be_uint8(instance->be_uint8, ustate);
    unparse_be_int64(instance->be_int64, ustate);
    unparse_be_int32(instance->be_int32, ustate);
    unparse_be_int16(instance->be_int16, ustate);
    unparse_be_int8(instance->be_int8, ustate);
    unparse_le_uint64(instance->le_uint64, ustate);
    unparse_le_uint32(instance->le_uint32, ustate);
    unparse_le_uint16(instance->le_uint16, ustate);
    unparse_le_uint8(instance->le_uint8, ustate);
    unparse_le_int64(instance->le_int64, ustate);
    unparse_le_int32(instance->le_int32, ustate);
    unparse_le_int16(instance->le_int16, ustate);
    unparse_le_int8(instance->le_int8, ustate);
    unparse_le_float(instance->le_float, ustate);
    unparse_le_double(instance->le_double, ustate);
}

