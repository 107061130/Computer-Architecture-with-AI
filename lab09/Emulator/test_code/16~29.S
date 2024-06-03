.text
.macro init

main:
li 	     sp, 10000  
li       t0, 0xff00fff0
li       t1, 5
bext     t2, t0, t1
bseti    t2, t0, 16
bclri    t2, t0, 5
binvi    t2, t0, 6
bexti    t2, t0, 5
ror      t2, t0, t1
rol      t2, t0, t1
rori     t2, t0, 5
sh1add   t2, t0, t1
sh2add   t2, t0, t1
sh3add   t2, t0, t1
rev8     t2, t0
zexth    t2, t0
orc.b    t2, t0

nop
nop
nop
nop
nop
hcf
