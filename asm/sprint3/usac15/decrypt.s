# decrypt.s - Caesar cipher decryption (x86-64 assembly, AT&T syntax)

.section .text
.globl decrypt_data
.type decrypt_data, @function

# int decrypt_data(char* in, int key, char* out)
# Arguments:
#   rdi = in (input string pointer)
#   rsi = key (integer)
#   rdx = out (output string pointer)
# Returns:
#   rax = 1 on success, 0 on failure

decrypt_data:
    pushq   %rbp
    movq    %rsp, %rbp
    pushq   %rbx
    pushq   %r12
    pushq   %r13
    
    # Save arguments
    movq    %rdi, %r12          # r12 = in
    movl    %esi, %ebx          # ebx = key
    movq    %rdx, %r13          # r13 = out
    
    # Check if input is empty
    movb    (%r12), %al
    testb   %al, %al
    jz      .L_empty_fail
    
    # Check if key is in range [1, 26]
    cmpl    $1, %ebx
    jl      .L_failure
    cmpl    $26, %ebx
    jg      .L_failure
    
    # Initialize counters
    xorq    %rcx, %rcx          # rcx = index = 0
    
.L_loop:
    # Load character
    movb    (%r12, %rcx), %al
    
    # Check for null terminator
    testb   %al, %al
    jz      .L_success
    
    # Check if character is uppercase A-Z
    cmpb    $'A', %al
    jl      .L_failure
    cmpb    $'Z', %al
    jg      .L_failure
    
    # Decrypt: (char - 'A' - key + 26) % 26 + 'A'
    subb    $'A', %al           # al = char - 'A'
    movzbl  %al, %eax           # zero extend
    subl    %ebx, %eax          # eax = (char - 'A') - key
    addl    $26, %eax           # eax = (char - 'A') - key + 26
    
    # Calculate modulo 26
    movl    $26, %edi
    xorl    %edx, %edx
    divl    %edi                # eax = quotient, edx = remainder
    
    addb    $'A', %dl           # dl = remainder + 'A'
    
    # Store decrypted character
    movb    %dl, (%r13, %rcx)
    
    # Increment index
    incq    %rcx
    jmp     .L_loop
    
.L_success:
    # Null terminate output
    movb    $0, (%r13, %rcx)
    movl    $1, %eax
    jmp     .L_end
    
.L_empty_fail:
    movb    $0, (%r13)
.L_failure:
    xorl    %eax, %eax
    
.L_end:
    popq    %r13
    popq    %r12
    popq    %rbx
    popq    %rbp
    ret

.size decrypt_data, .-decrypt_data
