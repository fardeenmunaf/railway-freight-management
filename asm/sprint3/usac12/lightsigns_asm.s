.section .rodata
msg_led_on:  
    .asciz "  [LED %d] Ligando...\n"
msg_led_off: 
    .asciz "  [LED %d] Desligando...\n"

.section .text
.global control_led

# int control_led(LedControl *led)
# a0 = ponteiro para struct LedControl { int led_id, int state, int count }
# Retorna: a0 = número de LEDs ligados
control_led:
    addi    sp, sp, -16
    sw      ra, 12(sp)
    sw      s0, 8(sp)
    sw      s1, 4(sp)
    
    mv      s0, a0              # Salvar ponteiro struct
    
    lw      t0, 4(s0)           # Carregar state (offset 4)
    li      t1, 1
    beq     t0, t1, turn_on     # Se state == 1, ligar
    
    # Desligar LED
    lw      a1, 0(s0)           # led_id como argumento
    la      a0, msg_led_off
    call    printf
    
    lw      t0, 8(s0)           # Carregar count
    addi    t0, t0, -1          # Decrementar
    bgez    t0, update_count    # Se >= 0, ok
    li      t0, 0               # Senão, count = 0
    j       update_count
    
turn_on:
    lw      a1, 0(s0)           # led_id como argumento
    la      a0, msg_led_on
    call    printf
    
    lw      t0, 8(s0)           # Carregar count
    addi    t0, t0, 1           # Incrementar
    
update_count:
    sw      t0, 8(s0)           # Atualizar count no struct
    mv      a0, t0              # Retornar count
    
    lw      s1, 4(sp)
    lw      s0, 8(sp)
    lw      ra, 12(sp)
    addi    sp, sp, 16
    ret

