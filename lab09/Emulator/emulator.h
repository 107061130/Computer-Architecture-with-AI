#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>

#ifndef EMULATOR_H__
#define EMULATOR_H__

// 64 KB
#define MEM_BYTES 0x10000
#define TEXT_OFFSET 0
#define DATA_OFFSET 32768

#define MAX_LABEL_COUNT 128
#define MAX_LABEL_LEN 32
#define MAX_SRC_LEN (1024 * 1024)

#define VECTOR_LEN 512
#define ELEMENT_WIDTH 8
#define VLMAX VECTOR_LEN / ELEMENT_WIDTH

typedef struct
{
	char *src;
	int offset;
} source;

/* Vector Struct for more than 64 bit */
typedef struct
{
	uint8_t bytes[VLMAX];
} vector_reg;

typedef enum
{
	UNIMPL = 0,

	// instruction added Tiger_Chang
	CLZ, // Tiger_Chang
	CTZ,
	CPOP,
	ANDN,
	MAX,
	CLMUL,
	CLMULH,
	CLMULR,
	//*****************
	// instruction added
	// funfish111065531
	MAXU, // unsiged max
	MIN,  // signed min
	MINU, // unsigned min
	ORN,  // OR with inverted operand
	ROL,  // Rotate Left (Register)
	ROR,  // Rotate Right (Register)

	ORC_B, // Bitwise OR-Combine, byte granule
	REV8,  // Byte-reverse register
	//*****************
	// 111064528 : 17~24
	RORI,
	BCLR,
	BCLRI,
	BEXT,
	BEXTI,
	BINV,
	BINVI,
	BSET,
	//*****************
	// 112062674
	BSETI,
	SEXTB,
	SEXTH,
	SH1ADD,
	SH2ADD,
	SH3ADD,
	XNOR,
	ZEXTH,
	//*****************

	ADD,
	ADDI,
	AND,
	ANDI,
	AUIPC,
	BEQ,
	BGE,
	BGEU,
	BLT,
	BLTU,
	BNE,
	JAL,
	JALR,
	LB,
	LBU,
	LH,
	LHU,
	LUI,
	LW,
	OR,
	ORI,
	SB,
	SH,
	SLL,
	SLLI,
	SLT,
	SLTI,
	SLTIU,
	SLTU,
	SRA,
	SRAI,
	SRL,
	SRLI,
	SUB,
	SW,
	XOR,
	XORI,
	HCF
} instr_type;

typedef enum
{
	OPTYPE_NONE, // more like "don't care"
	OPTYPE_REG,
	OPTYPE_IMM,
	OPTYPE_LABEL,
} operand_type;
typedef struct
{
	operand_type type = OPTYPE_NONE;
	char label[MAX_LABEL_LEN];
	int reg;
	uint32_t imm;

} operand;
typedef struct
{
	instr_type op;
	operand a1;
	operand a2;
	operand a3;
	char *psrc = NULL;
	int orig_line = -1;
	bool breakpoint = false;
} instr;

typedef struct
{
	char label[MAX_LABEL_LEN];
	int loc = -1;
} label_loc;

uint32_t mem_read(uint8_t *, uint32_t, instr_type);

#endif
