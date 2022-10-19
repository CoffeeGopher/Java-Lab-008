import java.util.HashMap;

public enum MorseCode {
    // 0 is a dot, 1 is a dash
    A(0,1),
    B(1,0,0,0),
    C(1,0,1,0),
    D(1,0,0),
    E(0),
    F(0,0,1,0),
    G(1,1,0),
    H(0,0,0,0),
    I(0,0),
    J(0,1,1,1),
    K(1,0,1),
    L(0,1,0,0),
    M(1,1),
    N(1,0),
    O(1,1,1),
    P(0,1,1,0),
    Q(1,1,0,1),
    R(0,1,0),
    S(0,0,0),
    T(1),
    U(0,0,1),
    V(0,0,0,1),
    W(0,1,1),
    X(1,0,0,1),
    Y(1,0,1,1),
    Z(1,1,0,0),

    ONE(0,1,1,1,1),
    TWO(0,0,1,1,1),
    THREE(0,0,0,1,1),
    FOUR(0,0,0,0,1),
    FIVE(0,0,0,0,0),
    SIX(1,0,0,0,0),
    SEVEN(1,1,0,0,0),
    EIGHT(1,1,1,0,0),
    NINE(1,1,1,1,0),
    ZERO(1,1,1,1,1),
    ;

    private final int[] morseCode;

    MorseCode(int... morseCode) {
        this.morseCode = morseCode;
    }

    public int[] getMorseCodeArray() {
        return morseCode;
    }
}
