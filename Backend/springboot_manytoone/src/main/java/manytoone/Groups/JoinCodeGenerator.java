package manytoone.Groups;

import java.security.SecureRandom;

public class JoinCodeGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 5;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate a random 5-character alphanumeric join code
     * Format: AB123, XY789, etc.
     */
    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
    
    /**
     * Generate a unique join code that doesn't exist in the database
     */
    public static String generateUniqueCode(GroupRepository groupRepository) {
        String code;
        do {
            code = generateCode();
        } while (groupRepository.existsByJoinCode(code));
        return code;
    }
}
