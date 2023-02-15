package at.muli.iolynprosser.dto;

import java.time.LocalDateTime;

public record Comment(String id,
                      String comment,
                      LocalDateTime dateAdd) {
}
