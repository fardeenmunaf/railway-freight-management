    .section .text
    .global median

# int median(int* vec, int length, int *me)
median:
    blez    a1, fail            # if length <= 0, return 0

# ----------------------- Insertion Sort -----------------------
    li      t0, 1               # i = 1
for_i:
    bge     t0, a1, end_sort    # if i >= length, end sort

    slli    t1, t0, 2           # t1 = i*4
    add     t1, a0, t1           # t1 = &vec[i]
    lw      t2, 0(t1)            # t2 = key

    addi    t3, t0, -1          # j = i-1
while_j:
    bltz    t3, insert_key      # if j < 0, insert key

    slli    t4, t3, 2           # t4 = j*4
    add     t4, a0, t4           # t4 = &vec[j]
    lw      t5, 0(t4)            # t5 = vec[j]

    ble     t5, t2, insert_key  # if vec[j] <= key, insert

    addi    t6, t4, 4           # &vec[j+1]
    sw      t5, 0(t6)           # vec[j+1] = vec[j]

    addi    t3, t3, -1          # j--
    j       while_j

insert_key:
    addi    t3, t3, 1           # j+1
    slli    t4, t3, 2
    add     t4, a0, t4           # &vec[j+1]
    sw      t2, 0(t4)           # vec[j+1] = key

    addi    t0, t0, 1           # i++
    j       for_i

# ----------------------- Calculate Median -----------------------
end_sort:
    andi    t0, a1, 1           # t0 = length % 2
    beqz    t0, even_case       # if even, go to even_case

# odd length
    srli    t1, a1, 1           # t1 = length / 2
    slli    t1, t1, 2           # t1 *= 4
    add     t1, a0, t1           # &vec[length/2]
    lw      t2, 0(t1)           # vec[length/2]
    sw      t2, 0(a2)           # *me = median
    li      a0, 1
    ret

# even length
even_case:
    addi    t1, a1, -1
    srli    t1, t1, 1           # (length-1)/2
    slli    t1, t1, 2
    add     t1, a0, t1           # &vec[(length-1)/2]
    lw      t2, 0(t1)            # first middle
    lw      t3, 4(t1)            # second middle
    add     t4, t2, t3
    srai    t4, t4, 1            # average
    sw      t4, 0(a2)            # *me = median
    li      a0, 1
    ret

fail:
    li      a0, 0
    ret
