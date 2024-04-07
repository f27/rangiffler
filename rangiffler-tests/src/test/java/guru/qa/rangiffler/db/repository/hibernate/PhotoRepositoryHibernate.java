package guru.qa.rangiffler.db.repository.hibernate;

import guru.qa.rangiffler.db.DataBase;
import guru.qa.rangiffler.db.entity.photo.PhotoEntity;
import guru.qa.rangiffler.db.jpa.EmfProvider;
import guru.qa.rangiffler.db.jpa.JpaService;
import guru.qa.rangiffler.db.jpa.ThreadLocalEntityManager;
import guru.qa.rangiffler.db.repository.PhotoRepository;

import java.util.UUID;

public class PhotoRepositoryHibernate extends JpaService implements PhotoRepository {

    public PhotoRepositoryHibernate() {
        super(new ThreadLocalEntityManager(EmfProvider.INSTANCE.emf(DataBase.PHOTO)));
    }

    @Override
    public PhotoEntity create(PhotoEntity photo) {
        persist(photo);
        return photo;
    }

    @Override
    public void deleteByUserId(UUID id) {
        String query = "SELECT p FROM PhotoEntity p WHERE p.userId=:id";
        tx(em -> em.createQuery(query, PhotoEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .forEach(photoEntity -> {
                    em.refresh(photoEntity);
                    em.remove(photoEntity);
                })
        );
    }
}
