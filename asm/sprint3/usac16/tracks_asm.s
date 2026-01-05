# tracks_asm.s - Assembly implementation for track assignment
# x86-64 assembly (AT&T syntax)

.section .rodata
msg_searching:
    .string "[ASM] Searching for available track...\n"
msg_found:
    .string "[ASM] Track %d assigned to train %s\n"
msg_emergency:
    .string "[ASM] EMERGENCY STOP - No tracks available!\n"

.section .text
.global assign_track_asm

# int assign_track_asm(TrackSystem *system, const char *train_id)
# rdi = pointer to TrackSystem struct
# rsi = pointer to train_id string
# Returns: rax = track number (1-8) if successful, -1 if emergency stop
#
# TrackSystem structure layout:
#   offset 0: Track tracks[8] - array of Track structs
#   Each Track struct (size = 32 bytes):
#     offset 0:  int track_id (4 bytes)
#     offset 4:  int state (4 bytes)
#     offset 8:  char train_id[20] (20 bytes)
#     offset 28: int occupied_time (4 bytes)
#   offset 256 (32*8): int total_tracks
#   offset 260: int free_tracks
#   offset 264: int occupied_tracks
#   offset 268: int non_operational_tracks
#   offset 272: int total_arrivals
#   offset 276: int total_departures
#   offset 280: int emergency_stops

assign_track_asm:
    pushq   %rbp
    movq    %rsp, %rbp
    pushq   %rbx
    pushq   %r12
    pushq   %r13
    pushq   %r14
    pushq   %r15
    subq    $8, %rsp            # Align stack to 16 bytes
    
    movq    %rdi, %r12          # r12 = TrackSystem pointer
    movq    %rsi, %r13          # r13 = train_id pointer
    
    # Print searching message
    leaq    msg_searching(%rip), %rdi
    xorq    %rax, %rax
    call    printf@PLT
    
    # Initialize loop variables
    xorq    %r14, %r14          # r14 = current index (0-7)
    movq    $8, %r15            # r15 = max tracks
    movq    $-1, %rbx           # rbx = selected track (-1 = none found)
    
search_loop:
    cmpq    %r15, %r14          # if index >= 8, exit loop
    jge     end_search
    
    # Calculate offset for current track: index * 32
    movq    %r14, %rax
    shlq    $5, %rax            # rax = index * 32
    leaq    (%r12,%rax), %rcx   # rcx = pointer to tracks[index]
    
    # Load track state (offset 4)
    movl    4(%rcx), %edx       # edx = track.state
    
    # Check if track is FREE (state == 0)
    testl   %edx, %edx
    jnz     next_track          # if state != 0, skip
    
    # Found a free track!
    movq    %r14, %rbx          # Save track index
    
    # Set track state to OCCUPIED (1)
    movl    $1, 4(%rcx)         # track.state = OCCUPIED
    
    # Copy train_id to track (offset 8)
    leaq    8(%rcx), %rdi       # dest = &track.train_id
    movq    %r13, %rsi          # src = train_id
    movq    $20, %rdx           # max 20 bytes
    call    strncpy@PLT
    
    # Calculate offset again (rcx might be clobbered)
    movq    %r14, %rax
    shlq    $5, %rax
    leaq    (%r12,%rax), %rcx
    
    # Reset occupied_time to 0 (offset 28)
    movl    $0, 28(%rcx)
    
    # Update TrackSystem statistics
    movl    260(%r12), %eax     # free_tracks
    subl    $1, %eax
    movl    %eax, 260(%r12)
    
    movl    264(%r12), %eax     # occupied_tracks
    addl    $1, %eax
    movl    %eax, 264(%r12)
    
    movl    272(%r12), %eax     # total_arrivals
    addl    $1, %eax
    movl    %eax, 272(%r12)
    
    # Print success message
    leaq    msg_found(%rip), %rdi
    movq    %rbx, %rsi
    addq    $1, %rsi            # track number (1-based)
    movq    %r13, %rdx          # train_id
    xorq    %rax, %rax
    call    printf@PLT
    
    jmp     end_search
    
next_track:
    addq    $1, %r14            # index++
    jmp     search_loop
    
end_search:
    # Check if a track was found
    cmpq    $-1, %rbx
    je      emergency_stop
    
    # Return track number (1-based)
    movq    %rbx, %rax
    addq    $1, %rax
    jmp     cleanup
    
emergency_stop:
    # No tracks available - emergency stop!
    leaq    msg_emergency(%rip), %rdi
    xorq    %rax, %rax
    call    printf@PLT
    
    # Update emergency_stops counter
    movl    280(%r12), %eax
    addl    $1, %eax
    movl    %eax, 280(%r12)
    
    # Return -1
    movq    $-1, %rax
    
cleanup:
    addq    $8, %rsp
    popq    %r15
    popq    %r14
    popq    %r13
    popq    %r12
    popq    %rbx
    popq    %rbp
    ret

# Mark stack as non-executable
.section .note.GNU-stack,"",@progbits
