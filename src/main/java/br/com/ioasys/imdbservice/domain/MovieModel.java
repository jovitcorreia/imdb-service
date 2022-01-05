package br.com.ioasys.imdbservice.domain;

import br.com.ioasys.imdbservice.util.YearAttributeConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@RequiredArgsConstructor
@Table(name = "movies")
@ToString
public class MovieModel implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(columnDefinition = "BINARY(36)")
  @GeneratedValue
  private UUID movieId;

  @Column(nullable = false)
  private String title;

  private String description;

  @Column(columnDefinition = "smallint")
  @Convert(converter = YearAttributeConverter.class)
  private Year release;

  @JoinTable(
      inverseJoinColumns = @JoinColumn(name = "genre_id"),
      joinColumns = @JoinColumn(name = "movie_id"),
      name = "movies_genres")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ManyToMany(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<GenreModel> genres;

  @JoinTable(
      inverseJoinColumns = @JoinColumn(name = "director_id"),
      joinColumns = @JoinColumn(name = "movie_id"),
      name = "movies_directors")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ManyToMany(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<PersonModel> directors;

  @JoinTable(
      inverseJoinColumns = @JoinColumn(name = "actor_id"),
      joinColumns = @JoinColumn(name = "movie_id"),
      name = "movies_actors")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ManyToMany(fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<PersonModel> actors;

  @Fetch(FetchMode.SUBSELECT)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie")
  @ToString.Exclude
  private Set<RatingModel> ratings;

  @Column(nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", shape = JsonFormat.Shape.STRING)
  private LocalDateTime createdDate;

  @Column(nullable = false)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", shape = JsonFormat.Shape.STRING)
  private LocalDateTime lastModifiedDate;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    MovieModel that = (MovieModel) object;
    return movieId.equals(that.movieId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(movieId);
  }

  public double getAverageRating() {
    return this.ratings.stream().mapToDouble(RatingModel::getStars).average().orElse(0.0);
  }
}
