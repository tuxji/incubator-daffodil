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

#include "parsers.h"
#include <endian.h> // for be64toh, le64toh, etc.
#include <stdio.h>  // for fread, size_t

// Functions to parse binary floating point numbers and integers

extern void
parse_be_double(double *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(double)];
            double   f_val;
            uint64_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        buffer.i_val = be64toh(buffer.i_val);
        *number = buffer.f_val;
    }
}

extern void
parse_be_float(float *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(float)];
            float    f_val;
            uint32_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        buffer.i_val = be32toh(buffer.i_val);
        *number = buffer.f_val;
    }
}

extern void
parse_be_uint64(uint64_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(uint64_t)];
            uint64_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = be64toh(buffer.i_val);
    }
}

extern void
parse_be_uint32(uint32_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(uint32_t)];
            uint32_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = be32toh(buffer.i_val);
    }
}

extern void
parse_be_uint16(uint16_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(uint16_t)];
            uint16_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = be16toh(buffer.i_val);
    }
}

extern void
parse_be_uint8(uint8_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(uint8_t)];
            uint8_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = buffer.i_val;
    }
}

extern void
parse_be_int64(int64_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(int64_t)];
            int64_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = be64toh(buffer.i_val);
    }
}

extern void
parse_be_int32(int32_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(int32_t)];
            int32_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = be32toh(buffer.i_val);
    }
}

extern void
parse_be_int16(int16_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(int16_t)];
            int16_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = be16toh(buffer.i_val);
    }
}

extern void
parse_be_int8(int8_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char   c_val[sizeof(int8_t)];
            int8_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = buffer.i_val;
    }
}

extern void
parse_le_double(double *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(double)];
            double   f_val;
            uint64_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        buffer.i_val = le64toh(buffer.i_val);
        *number = buffer.f_val;
    }
}

extern void
parse_le_float(float *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(float)];
            float    f_val;
            uint32_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        buffer.i_val = le32toh(buffer.i_val);
        *number = buffer.f_val;
    }
}

extern void
parse_le_uint64(uint64_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(uint64_t)];
            uint64_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = le64toh(buffer.i_val);
    }
}

extern void
parse_le_uint32(uint32_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(uint32_t)];
            uint32_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = le32toh(buffer.i_val);
    }
}

extern void
parse_le_uint16(uint16_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char     c_val[sizeof(uint16_t)];
            uint16_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = le16toh(buffer.i_val);
    }
}

extern void
parse_le_uint8(uint8_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(uint8_t)];
            uint8_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = buffer.i_val;
    }
}

extern void
parse_le_int64(int64_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(int64_t)];
            int64_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = le64toh(buffer.i_val);
    }
}

extern void
parse_le_int32(int32_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(int32_t)];
            int32_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = le32toh(buffer.i_val);
    }
}

extern void
parse_le_int16(int16_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char    c_val[sizeof(int16_t)];
            int16_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = le16toh(buffer.i_val);
    }
}

extern void
parse_le_int8(int8_t *number, PState *pstate)
{
    if (!pstate->error_msg)
    {
        union
        {
            char   c_val[sizeof(int8_t)];
            int8_t i_val;
        } buffer;
        size_t count = fread(&buffer.c_val, 1, sizeof(buffer), pstate->stream);
        if (count < sizeof(buffer))
        {
            pstate->error_msg = eof_or_error_msg(pstate->stream);
        }
        *number = buffer.i_val;
    }
}
