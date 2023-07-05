package project.account;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

public record Transaction(UUID senderAccountId, UUID recipientAccountId, LocalDateTime date,
                          BigDecimal amount, Currency currency ) implements Serializable { }
