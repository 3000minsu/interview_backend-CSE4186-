package CSE4186.interview.repository;

import CSE4186.interview.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.Arrays;
import java.util.List;

import static CSE4186.interview.entity.QPost.post;
import static CSE4186.interview.entity.QUser.user;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<Post> findPostsBySearchCondition(String q, String condition, Pageable pageable) {
        List<Post> content = queryFactory.
                selectFrom(post)
                .leftJoin(post.user, user)
                .where(usernameContain(q, condition),
                        postTitleContain(q, condition),
                        postFieldContain(q, condition))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.user, user)
                .where(usernameContain(q, condition),
                        postTitleContain(q, condition));

        return PageableExecutionUtils.getPage(content, pageable, total::fetchOne);
    }

    private BooleanExpression usernameContain(String username, String condition) {
        if (condition.equals("username")) {
            return isEmpty(username) ? null : user.name.contains(username);
        }
        return null;
    }

    private BooleanExpression postTitleContain(String title, String condition) {
        if (condition.equals("title")) {
            return isEmpty(title) ? null : post.title.contains(title);
        }
        return null;
    }

    private BooleanExpression postFieldContain(String field, String condition) {
        if (condition.equals("field")) {
            return isEmpty(field) ? null : post.jobField.symbol.in(Arrays.asList(field.split(",")));
        }
        return null;
    }
}
