package com.touhouqing.chatAiDemo;

import com.touhouqing.chatAiDemo.entity.Movie;
import com.touhouqing.chatAiDemo.entity.Person;
import com.touhouqing.chatAiDemo.entity.PersonRelationship;
import com.touhouqing.chatAiDemo.entity.Roles;
import com.touhouqing.chatAiDemo.repository.MovieRepository;
import com.touhouqing.chatAiDemo.repository.PersonRelationshipRepository;
import com.touhouqing.chatAiDemo.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
class ChatAiDemoApplicationTests {

    @Autowired
    PersonRepository personRepository;
    
    @Autowired
    PersonRelationshipRepository personRelationshipRepository;
    
    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    public void setUp() {
        // 清理数据
        movieRepository.deleteAll();
        personRepository.deleteAll();
        personRelationshipRepository.deleteAll();
    }

    @Test
    public void testCreatePersons() {
        // 测试创建人员（演员和导演）
        Person actor1 = new Person("基努·里维斯", 1964);
        Person actor2 = new Person("劳伦斯·费舍伯恩", 1967);
        Person director1 = new Person("拉娜·沃卓斯基", 1965);
        Person director2 = new Person("莉莉·沃卓斯基", 1967);

        // 保存人员
        personRepository.save(actor1);
        personRepository.save(actor2);
        personRepository.save(director1);
        personRepository.save(director2);

        // 验证保存结果
        assertEquals(4, personRepository.count());
        
        List<Person> foundPersons = personRepository.findByName("基努·里维斯");
        assertFalse(foundPersons.isEmpty());
        assertEquals("基努·里维斯", foundPersons.get(0).getName());
        assertEquals(1964, foundPersons.get(0).getBorn());
    }

    @Test
    public void testCreateMoviesWithRelationships() {
        // 创建人员
        Person keanu = new Person("基努·里维斯", 1964);
        Person laurence = new Person("劳伦斯·费舍伯恩", 1967);
        Person lana = new Person("拉娜·沃卓斯基", 1965);
        Person lily = new Person("莉莉·沃卓斯基", 1967);

        personRepository.save(keanu);
        personRepository.save(laurence);
        personRepository.save(lana);
        personRepository.save(lily);

        // 创建电影
        Movie matrix = new Movie("黑客帝国", "一部关于虚拟现实的科幻电影");
        
        // 添加演员和角色关系
        Roles neoRole = new Roles(keanu, "尼奥");
        Roles morpheusRole = new Roles(laurence, "墨菲斯");
        matrix.getActorsAndRoles().add(neoRole);
        matrix.getActorsAndRoles().add(morpheusRole);
        
        // 添加导演关系
        matrix.getDirectors().add(lana);
        matrix.getDirectors().add(lily);

        // 保存电影
        movieRepository.save(matrix);

        // 验证保存结果
        assertEquals(1, movieRepository.count());
        
        Optional<Movie> foundMovie = movieRepository.findByTitle("黑客帝国");
        assertTrue(foundMovie.isPresent());
        assertEquals("黑客帝国", foundMovie.get().getTitle());
        assertEquals("一部关于虚拟现实的科幻电影", foundMovie.get().getDescription());
        assertEquals(2, foundMovie.get().getActorsAndRoles().size());
        assertEquals(2, foundMovie.get().getDirectors().size());
    }

    @Test
    public void testQueryMoviesByActor() {
        // 准备测试数据
        setupTestData();

        // 测试根据演员查询电影
        List<Movie> keanuMovies = movieRepository.findMoviesByActor("基努·里维斯");
        assertFalse(keanuMovies.isEmpty());
        assertTrue(keanuMovies.stream().anyMatch(m -> m.getTitle().equals("黑客帝国")));
    }

    @Test
    public void testQueryMoviesByDirector() {
        // 准备测试数据
        setupTestData();

        // 测试根据导演查询电影
        List<Movie> lanaMovies = movieRepository.findMoviesByDirector("拉娜·沃卓斯基");
        assertFalse(lanaMovies.isEmpty());
        assertTrue(lanaMovies.stream().anyMatch(m -> m.getTitle().equals("黑客帝国")));
    }

    @Test
    public void testUpdateMovie() {
        // 创建电影
        Movie movie = new Movie("测试电影", "原始描述");
        movieRepository.save(movie);

        // 查找并更新
        Optional<Movie> foundMovie = movieRepository.findByTitle("测试电影");
        assertTrue(foundMovie.isPresent());
        
        Movie updatedMovie = new Movie("测试电影", "更新后的描述");
        movieRepository.save(updatedMovie);

        // 验证更新结果
        Optional<Movie> updatedFoundMovie = movieRepository.findByTitle("测试电影");
        assertTrue(updatedFoundMovie.isPresent());
        assertEquals("更新后的描述", updatedFoundMovie.get().getDescription());
    }

    @Test
    public void testDeleteMovie() {
        // 创建电影
        Movie movie = new Movie("待删除电影", "这部电影将被删除");
        movieRepository.save(movie);

        // 验证创建成功
        assertTrue(movieRepository.findByTitle("待删除电影").isPresent());

        // 删除电影
        movieRepository.delete(movie);

        // 验证删除成功
        assertFalse(movieRepository.findByTitle("待删除电影").isPresent());
    }

    @Test
    public void testDeletePerson() {
        // 创建人员
        Person person = new Person("测试演员", 1980);
        personRepository.save(person);

        // 验证创建成功
        assertFalse(personRepository.findByName("测试演员").isEmpty());

        // 删除人员
        personRepository.delete(person);

        // 验证删除成功
        assertTrue(personRepository.findByName("测试演员").isEmpty());
    }

    @Test
    public void testComplexRelationshipQuery() {
        // 准备复杂的测试数据
        setupComplexTestData();

        // 测试复杂查询 - 查找参与多部电影的演员
        List<Movie> keanuMovies = movieRepository.findMoviesByActor("基努·里维斯");
        assertTrue(keanuMovies.size() >= 2); // 应该参与了至少2部电影

        // 测试查找电影的完整演员阵容
        List<Movie> matrixWithCast = movieRepository.findMovieWithCast("黑客帝国");
        assertFalse(matrixWithCast.isEmpty());
    }

    @Test
    public void testPersonRelationship() {
        // 创建人员关系
        PersonRelationship relationship = new PersonRelationship();
        relationship.setType("合作关系");
        
        personRelationshipRepository.save(relationship);
        
        // 验证关系创建
        assertEquals(1, personRelationshipRepository.count());
        
        List<PersonRelationship> relationships = personRelationshipRepository.findAll();
        assertFalse(relationships.isEmpty());
        assertEquals("合作关系", relationships.get(0).getType());
    }

    // 辅助方法：设置基础测试数据
    private void setupTestData() {
        Person keanu = new Person("基努·里维斯", 1964);
        Person laurence = new Person("劳伦斯·费舍伯恩", 1967);
        Person lana = new Person("拉娜·沃卓斯基", 1965);

        personRepository.save(keanu);
        personRepository.save(laurence);
        personRepository.save(lana);

        Movie matrix = new Movie("黑客帝国", "科幻电影");
        Roles neoRole = new Roles(keanu, "尼奥");
        matrix.getActorsAndRoles().add(neoRole);
        matrix.getDirectors().add(lana);

        movieRepository.save(matrix);
    }

    // 辅助方法：设置复杂测试数据
    private void setupComplexTestData() {
        // 创建人员
        Person keanu = new Person("基努·里维斯", 1964);
        Person laurence = new Person("劳伦斯·费舍伯恩", 1967);
        Person lana = new Person("拉娜·沃卓斯基", 1965);
        Person lily = new Person("莉莉·沃卓斯基", 1967);

        personRepository.save(keanu);
        personRepository.save(laurence);
        personRepository.save(lana);
        personRepository.save(lily);

        // 创建多部电影
        Movie matrix1 = new Movie("黑客帝国", "第一部");
        Roles neo1 = new Roles(keanu, "尼奥");
        Roles morpheus1 = new Roles(laurence, "墨菲斯");
        matrix1.getActorsAndRoles().add(neo1);
        matrix1.getActorsAndRoles().add(morpheus1);
        matrix1.getDirectors().add(lana);
        matrix1.getDirectors().add(lily);

        Movie matrix2 = new Movie("黑客帝国2：重装上阵", "第二部");
        Roles neo2 = new Roles(keanu, "尼奥");
        matrix2.getActorsAndRoles().add(neo2);
        matrix2.getDirectors().add(lana);
        matrix2.getDirectors().add(lily);

        movieRepository.save(matrix1);
        movieRepository.save(matrix2);
    }
}
