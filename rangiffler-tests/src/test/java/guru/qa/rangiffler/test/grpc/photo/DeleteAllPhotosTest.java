package guru.qa.rangiffler.test.grpc.photo;

import guru.qa.grpc.rangiffler.grpc.DeleteAllPhotosRequest;
import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.repository.PhotoRepository;
import guru.qa.rangiffler.db.repository.hibernate.PhotoRepositoryHibernate;
import guru.qa.rangiffler.jupiter.annotation.GenerateUser;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserModel;
import guru.qa.rangiffler.test.grpc.BaseGrpcTest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.rangiffler.jupiter.annotation.User.GenerationType.FOR_GENERATE_USER;
import static io.qameta.allure.Allure.step;
import static java.lang.Thread.sleep;

@Feature("PHOTO")
@Story("DeleteAllPhotos")
@DisplayName("DeleteAllPhotos")
public class DeleteAllPhotosTest extends BaseGrpcTest {
    private final PhotoRepository photoRepository = new PhotoRepositoryHibernate();

    @Test
    @GenerateUser(photos = {
            @Photo,
            @Photo,
    })
    @DisplayName("Удаление всех фотографий пользователя с корректным запросом не должно выбрасывать исключение")
    void deleteAllPhotosTest(@User(FOR_GENERATE_USER) UserModel user) {
        DeleteAllPhotosRequest request = DeleteAllPhotosRequest.newBuilder()
                .setUserId(user.id().toString())
                .build();
        step("Отправить запрос и проверить, что нет исключений",
                () -> Assertions.assertDoesNotThrow(
                        () -> photoGrpcClient.deleteAllPhotos(request))
        );
        step("Фотографий не должно быть в БД", () -> {
            sleep(50);
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(0, allUsersPhotos.size());
        });
    }

    @Test
    @GenerateUser(photos = {
            @Photo,
            @Photo,
    })
    @DisplayName("Удаление всех фотографий пользователя с некорректным user id должно возвращать INVALID_ARGUMENT")
    void deleteAllPhotosWithIncorrectUserIdTest(@User(FOR_GENERATE_USER) UserModel user) {
        DeleteAllPhotosRequest request = DeleteAllPhotosRequest.newBuilder()
                .setUserId("")
                .build();
        Exception e = Assertions.assertThrows(StatusRuntimeException.class,
                () -> photoGrpcClient.deleteAllPhotos(request)
        );
        Assertions.assertEquals(
                Status.INVALID_ARGUMENT.withDescription("Bad user id").asRuntimeException().getMessage(),
                e.getMessage());
        step("Фотографии должны быть в БД", () -> {
            List<PhotoEntity> allUsersPhotos = photoRepository.findByUserId(user.id());
            Assertions.assertEquals(2, allUsersPhotos.size());
        });
    }
}
