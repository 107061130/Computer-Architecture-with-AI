(NTHU_111064528_張瀚) ACAL 2024 Spring Lab 7 HW 
===


## Gitlab code link

- Gitlab link - https://course.playlab.tw/git/111064528/lab07

## Hw 7-1 - RISC-V M-Standard Extension
### C code - MUL 
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  MUL,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "mul")) return MUL;
}

//line 522
switch( op ) {
    case UNIMPL: return 1;
    case MUL:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case MUL: rf[i.a1.reg] = rf[i.a2.reg] * rf[i.a3.reg]; break;
}

```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
addi x28,x0 ,2     ## x28 = 2
addi x29,x0 ,3     ## x29 = 3
mul  x30,x28,x29   ## x30 = 2*3 = 6
hcf                ## Terminate
```
- Simulation result
    ![](https://course.playlab.tw/md/uploads/caa38c6b-01bf-49ae-93e0-6dee5f1d0c69.png)
    
### C code - MULHU 
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  MULHU,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "mulhu")) return MULHU;
}

//line 522
switch( op ) {
    case UNIMPL: return 1;
    case MULHU:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case MULHU: 
        // use upper to store 64 bit result
        upper = (uint64_t)rf[i.a2.reg] * (uint64_t)rf[i.a3.reg];
        upper = upper >> 32;
        rf[i.a1.reg] = upper; 
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
addi x28,x0 , -1    ## x28 = 0xffffffff
addi x29,x0 , -1    ## x29 = 0xffffffff
mul  x30,x28,x29    ## x30 = 0xfffffffe / 00000001
```
- Simulation result
![](https://course.playlab.tw/md/uploads/50c24159-0482-407a-90e3-76b9749297d0.png)



### C code - REM
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  REM,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "rem")) return REM;
}

//line 522
switch( op ) {
    case UNIMPL: return 1;
    case REM:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case REM: 
        // if divisor is zero, assign value with dividend
        if (rf[i.a3.reg] == 0) 
            rf[i.a1.reg] = rf[i.a2.reg];
        else {
            // most significant bit of rs1, rs2
            msb_rs1 = (rf[i.a2.reg] >> 31) & 1; 
            msb_rs2 = (rf[i.a3.reg] >> 31) & 1;
            // caculate absolute value of rs1, rs2
            if (msb_rs1) urs1 = ~rf[i.a2.reg] + 1;
            else urs1 = rf[i.a2.reg];

            if (msb_rs2) urs2 = ~rf[i.a3.reg] + 1;
            else urs2 = rf[i.a3.reg];
            
            // if dividend is negative, the result is negative
            rf[i.a1.reg] = msb_rs1 ? -(urs1 % urs2) : urs1 % urs2;
        }
                break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
## 5 / 0 = 5
addi x28,x0 , 5
addi x29,x0 , 0
rem  x30,x28,x29

## 5 / 3 = 2
addi x28,x0 , 5
addi x29,x0 , 3
rem  x30,x28,x29

## -5 / 3 = -2
addi x28,x0 , -5
addi x29,x0 , 3
rem  x30,x28,x29

## 5 / -3 = 2
addi x28,x0 , 5
addi x29,x0 , -3
rem  x30,x28,x29

## -5 / -3 = -2
addi x28,x0 , -5
addi x29,x0 , -3
rem  x30,x28,x29
```

- Simulation result
![](https://course.playlab.tw/md/uploads/e6603192-db53-4ede-aa44-821cc9c16297.png)
![](https://course.playlab.tw/md/uploads/ded2fb29-4199-489b-8634-8cf11227c43e.png)


### C code - REMU 
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  REMU,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "remu")) return REMU;
}

//line 522
switch( op ) {
    case UNIMPL: return 1;
    case REMU:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case REMU:
        // most significant bit of rs1, rs2
        msb_rs1 = (rf[i.a2.reg] >> 31) & 1; 
        msb_rs2 = (rf[i.a3.reg] >> 31) & 1;
        // if negative, print warning
        if (msb_rs1 || msb_rs2) {
            printf("need unsigned vaule as input\n");
        }
        else {
            // if divisor is zero, assign dividend
            if (rf[i.a3.reg] == 0) rf[i.a1.reg] = rf[i.a2.reg];
            else rf[i.a1.reg] = rf[i.a2.reg] % rf[i.a3.reg];
        } 
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
## 5 / 0 = 5
addi x28,x0 , 5
addi x29,x0 , 0
rem  x30,x28,x29

## 5 / 3 = 2
addi x28,x0 , 5
addi x29,x0 , 3
rem  x30,x28,x29

## -5 / 3 not feasible
addi x28,x0 , -5
addi x29,x0 , 3
rem  x30,x28,x29

## 5 / -3 not feasible
addi x28,x0 , 5
addi x29,x0 , -3
rem  x30,x28,x29

## -5 / -3 not feasible
addi x28,x0 , -5
addi x29,x0 , -3
rem  x30,x28,x29
```
- Simulation result
![](https://course.playlab.tw/md/uploads/a8595901-c18e-4e4c-b341-d52ab436e0e4.png)
![](https://course.playlab.tw/md/uploads/38bf412d-cbc2-4140-acad-2515448f36d2.png)



## HW7-2 - RISC-V Bit Manipulation Extension
### Gitlab code link (Your own branch)

- Gitlab link of your branch - https://course.playlab.tw/git/funfish111065531/lab07-group/-/tree/111064528

- Gitlab link of your group project repo - https://course.playlab.tw/git/funfish111065531/lab07-group/-/tree/main?ref_type=heads

### C code - RORI 
```cpp=
// parse unsigned immediate value
uint32_t parse_uimm(char* tok, int bits, int line, bool strict = true) {
	if ( !(tok[0]>='0'&&tok[0]<='9') && strict) {
		print_syntax_error(line, "Malformed immediate value" );
	}
	long int imml = strtol(tok, NULL, 0);

	if (imml > ((1<<bits)-1) || imml < 0) {
		printf( "Syntax error at token %s\n", tok);
		exit(1);
	}
	uint64_t uv = *(uint64_t*)&imml;
	uint32_t hv = (uv&UINT32_MAX);

	return hv;
}

// Line 48
typedef enum {
UNIMPL = 0,
   RORI ,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "rori")) return  RORI ;
}

//line 522
switch( op ) {
    case RORI:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
            i->a1.reg = parse_reg(o1 , line);
            i->a2.reg = parse_reg(o2 , line);
            i->a3.imm = parse_uimm(o3, 5, line);
         return 1;
}

//line 642
switch (i.op) {
    case RORI: 
        // rs1 rotate right with immediate value
        rf[i.a1.reg] = (rf[i.a2.reg] >> i.a3.imm) | (rf[i.a2.reg] << (32 - i.a3.imm)); 
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567  ## x28 = 0xffffa3f1

## x29 = 0xffffa3f1 rotate 4 bit = 0x1ffffa3f
rori x29, x28, 4
## x29 = 0xffffa3f1 rotate 24 bit = 0xffa3f1ff
rori x29, x28, 24
## x29 = 0xffffa3f1 rotate 28 bit = 0xfffa3f1f
rori x29, x28, 28

```
- Simulation result
![](https://course.playlab.tw/md/uploads/18920f6e-1e51-40c9-8c17-6b6f40c0f53f.png)


### C code - BCLR
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  BCLR,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "bclr")) return BCLR;
}

//line 522
switch( op ) {
    case BCLR:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case BCLR: 
        // rs1 with a single bit cleared at the index specified in rs2
        index = rf[i.a3.reg] & 31;
        rf[i.a1.reg] = rf[i.a2.reg] & ~(1 << index);
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567    ## x28 = 0xffffa3f1
addi x29. x0, 31    ## x29 = 31
bclr x30, x28, x29  ## x30 = 0x7ffffa3f1

hcf
```
- Simulation result
![](https://course.playlab.tw/md/uploads/c111f56b-5973-479e-92d3-dbe84ba523b5.png)

### C code - BCLRI
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  BCLRI,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "bclri")) return BCLRI;
}

//line 522
switch( op ) {
    case BCLRI:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case BCLRI: 
        // use upper to store 64 bit result
        upper = (uint64_t)rf[i.a2.reg] * (uint64_t)rf[i.a3.reg];
        upper = upper >> 32;
        rf[i.a1.reg] = upper; 
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567     ## x28 = 0xffffa3f1
bclri x29, x28, 31   ## x30 = 0x7fffa3f1

hcf
```
- Simulation result
![](https://course.playlab.tw/md/uploads/60f11fad-c969-43d1-8c06-97ea6218eef2.png)

### C code - BEXT
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  BEXT,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "bext")) return BEXT;
}

//line 522
switch( op ) {
    case BEXT:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case BEXT:
        // single bit extracted from rs1 at the index specified in rs2.
        index = rf[i.a3.reg] & 31;
        rf[i.a1.reg] = (rf[i.a2.reg] >> index) & 1;
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567     ## x28 = 0xffffa3f1  
addi x29, x0, 4      ## x29 = 4
bext x30, x28, x29   ## x30 = 1 

hcf
```
- Simulation result
![](https://course.playlab.tw/md/uploads/ff5219b8-b28a-4c35-9f66-79886fc099a4.png)


### C code - BEXTI 
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  BEXTI,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "bexti")) return BEXTI;
}

//line 522
switch( op ) {
    case UNIMPL: return 1;
    case BEXTI:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case BEXTI: 
        // single bit extracted from rs1 at the index of immediate value
        rf[i.a1.reg] = (rf[i.a2.reg] >> i.a3.imm) & 1;
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567    ## x28 = 0xffffa3f1 
bexti x29, x28, 31  ## x30 = 1

hcf
```
- Simulation result
![](https://course.playlab.tw/md/uploads/beae1f5e-a0a1-49c4-a614-0b1736cba854.png)

### C code - BINV 
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  BINV,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "binv")) return BINV;
}

//line 522
switch( op ) {
    case UNIMPL: return 1;
    case BINV:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case BINV: 
        // rs1 with a single bit inverted at the index specified in rs2
        index = rf[i.a3.reg] & 31;
        rf[i.a1.reg] = rf[i.a2.reg] ^ (1 << index);
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567     ## x28 = 0xffffa3f1      
addi x29, x0, 4      ## x29 = 4
binv x30, x28, x29   ## x30 = 0xffffa3e1

hcf
```
- Simulation result
![](https://course.playlab.tw/md/uploads/891bd086-434d-400f-ae08-e757b99371a9.png)

### C code - BINVI
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  BINVI,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "binvi")) return BINVI;
}

//line 522
switch( op ) {
    case BINVI:
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case BINVI: 
        // rs1 with a single bit inverted at the index of immediate value
        rf[i.a1.reg] = rf[i.a2.reg] ^ (1 << i.a3.imm);
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567     ## x28 = 0xffffa3f1
binvi x29, x28, 4    ## x29 = 0xffffa3e1

hcf
```
- Simulation result
![](https://course.playlab.tw/md/uploads/1ae30f6f-0577-41aa-bf37-e32d18d29b00.png)

### C code - BSET 
```cpp=
// Line 48
typedef enum {
UNIMPL = 0,
  BSET,  
} instr_type;

//line 95
instr_type parse_instr(char* tok) {
  if ( streq(tok , "bset")) return BSET ;
}

//line 522
switch( op ) {
    case BSET :
        if ( !o1 || !o2 || !o3 || o4 ) print_syntax_error( line,  "Invalid format" );
        i->a1.reg = parse_reg(o1 , line);
        i->a2.reg = parse_reg(o2 , line);
        i->a3.reg = parse_reg(o3 , line);
        return 1;
}

//line 642
switch (i.op) {
    case BSET: 
        //  rs1 with a single bit set at the index specified in rs2
        index = rf[i.a3.reg] & 31;
        rf[i.a1.reg] = rf[i.a2.reg] | (1 << index);
        break;
}
```
#### Simulation Result & Assembly Code

- Assembly code to test MUL function
```mipsasm=
main:
li   x28, -23567      ## x28 = 0xffffa3f1  
addi x29, x0, 3       ## x28 = 3
bset x30, x28, x29    ## x30 = 0xffffa3f9 

hcf
```
- Simulation result

![](https://course.playlab.tw/md/uploads/5b0ee91a-f2ba-4fc0-81ed-694ea8e64e46.png)


