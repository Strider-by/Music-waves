package by.musicwaves.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.SQLRequestHandler;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.entity.ancillary.Country;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class UserDao implements Dao<Integer, User>
{
    private final static Logger LOGGER = LogManager.getLogger();
    public final static String SQL_SELECT_ALL
            = "SELECT * FROM users ";
    public final static String SQL_POSTFIX_SELECT_BY_ID
            = " WHERE user_id = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NICKNAME
            = " WHERE nickname = ?";
    public final static String SQL_POSTFIX_SELECT_BY_EMAIL
            = " WHERE email = ?";
    public final static String SQL_POSTFIX_SELECT_BY_ROLE
            = " WHERE role = ?";
    public final static String SQL_POSTFIX_SELECT_BY_COUNTRY
            = " WHERE country = ?";
    public final static String SQL_POSTFIX_SELECT_BY_LANGUAGE
            = " WHERE language = ?";

    public final static String SQL_CREATE_INSTANCE
            = "INSERT INTO users ("
            + "email, password, nickname, sex, first_name, last_name, language, "
            + "country, active, conf_code, avatar, role, register_date) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public final static String SQL_UPDATE_INSTANCE
            = "UPDATE users SET email = ?, password = ?, nickname = ?, sex = ?, "
            + "first_name = ?, last_name = ?, language = ?, country = ?, active = ?, "
            + "conf_code = ?, avatar = ?, role = ?, register_date = ?";

    public final static String SQL_DELETE_INSTANCE
            = "DELETE FROM users";

    public final static String SQL_USERS_FORM_FILTER_AND_SELECT
            = "SELECT * FROM music_waves.users WHERE email LIKE ? AND IFNULL(nickname, '') LIKE ?"
            + " AND register_date LIKE ?";

    public final static String SQL_UPDATE_ADMIN_ACCESSIBLE_FIELDS
            = "UPDATE music_waves.users SET email = ?, nickname = ?, role = ?";

    public final static String SQL_ROLE = "role";
    public final static String SQL_ID = "user_id";

    private final SQLRequestHandler requestHandler = new SQLRequestHandler();

    @Override
    public List<User> findAll() throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL,
                User::new,
                this::initUser,
                null);
    }

    @Override
    public User findById(Integer id) throws DaoException
    {
        return requestHandler.processSingleResultSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ID,
                User::new,
                this::initUser,
                (statement) -> statement.setNextInt(id));
    }

    public User findByEmail(String email) throws DaoException
    {
        return requestHandler.processSingleResultSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_EMAIL,
                User::new,
                this::initUser,
                (statement) -> statement.setNextString(email));
    }

    @Override
    public Integer create(User instance) throws DaoException
    {
        return requestHandler.processCreateRequest(
                instance,
                SQL_CREATE_INSTANCE,
                this::initCreationStatement);
    }

    @Override
    public User update(User instance) throws DaoException
    {
        User responce = requestHandler.processUpdateRequest(
                instance,
                SQL_UPDATE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                this::initCreationStatement,
                (statement) -> statement.setNextInt(instance.getId()));
        return responce;
    }

    @Override
    public boolean delete(Integer id) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(id, SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID);

        return result;
    }

    @Override
    public boolean delete(User instance) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(
                instance,
                SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                (inst, statement) -> statement.setNextInt(inst.getId()));

        return result;
    }

    public List<User> filterAndFind(String date, String email, String nickname, 
            Integer id, Integer roleId, int limit, int offset) throws DaoException
    {
        StringBuilder sql = new StringBuilder();
        sql.append(SQL_USERS_FORM_FILTER_AND_SELECT);
        if (id != null)
        {
            sql.append(SQL_AND).append(SQL_ID).append(SQL_EQUALS);
        }
        if (roleId != null && roleId != -1)
        {
            sql.append(SQL_AND).append(SQL_ROLE).append(SQL_EQUALS);
        }
        sql.append(SQL_LIMIT_OFFSET);
        LOGGER.debug("row sql request is: " + sql);

        return requestHandler.processMultipleResultsSelectRequest(
                sql.toString(),
                User::new,
                this::initUser,
                (statement) ->
        {
            statement.setNextString("%" + email + "%");
            statement.setNextString("%" + nickname + "%");
            statement.setNextString("%" + date + "%");
            if (id != null)
            {
                statement.setNextInt(id);
            }
            if (roleId != null && roleId != -1)
            {
                statement.setNextInt(roleId);
            }
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public boolean updateAdminAccessibleUserData(int id, String nickname, String email, int role) throws DaoException
    {
        int rowsAffected = requestHandler.processCustomUpdateRequest(
                SQL_UPDATE_ADMIN_ACCESSIBLE_FIELDS + SQL_POSTFIX_SELECT_BY_ID,
                (statement) ->
        {
            statement.setNextString(email);
            statement.setNextString(nickname);
            statement.setNextInt(role);
            statement.setNextInt(id);
        });

        return rowsAffected == 1;
    }

    private void initUser(User user, ResultSet resultSet) throws SQLException
    {
        user.setId(resultSet.getInt("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setHashedPassword(resultSet.getString("password"));
        user.setNickname(resultSet.getString("nickname"));
        user.setSex(User.Sex.parseDatabaseEquivalent(resultSet.getInt("sex")));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setLanguage(Language.parseDatabaseEquivalent(resultSet.getInt("language")));
        user.setCountry(Country.parseDatabaseEquivalent(resultSet.getInt("country")));
        user.setAccountActivated(resultSet.getBoolean("active"));
        user.setConfCode(resultSet.getString("conf_code"));
        user.setAvatarFileName(resultSet.getString("avatar"));
        user.setRole(Role.parseDatabaseEquivalent(resultSet.getInt("role")));
        user.parseAndSetRegisterDate(resultSet.getString("register_date"));
    }

    private void initCreationStatement(User user, PreparedStatementContainer statement) throws SQLException
    {
        statement.setNextString(user.getEmail());
        statement.setNextString(user.getHashedPassword());
        statement.setNextString(user.getNickname());
        statement.setNextInt(user.getSex().getDatabaseEquivalent());
        statement.setNextString(user.getFirstName());
        statement.setNextString(user.getLastName());
        statement.setNextInt(user.getLanguage().getDatabaseEquivalent());
        statement.setNextInt(user.getCountry().getDatabaseEquivalent());
        statement.setNextBoolean(user.isAccountActivated());
        statement.setNextString(user.getConfCode());
        statement.setNextString(user.getAvatarFileName());
        statement.setNextInt(user.getRole().getDatabaseEquivalent());
        statement.setNextString(user.getRegisterDate().toString());
    }

    @Override
    public void close()
    {
        requestHandler.close();
    }
}
