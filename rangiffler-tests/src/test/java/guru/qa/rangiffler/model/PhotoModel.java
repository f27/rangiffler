package guru.qa.rangiffler.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PhotoModel {

    private UUID id;
    private CountryEnum country;
    private String description;
    private String photo;

}
