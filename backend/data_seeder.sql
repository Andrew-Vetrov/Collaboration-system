-- docker exec -i postgres_db psql -U user -d colaboration_system_db < ./data_seeder.sql

DO $$
DECLARE
    user_uuid UUID := '58084c16-21f3-4d1e-a01a-8ce89ea9b93f'::UUID;
    project_ids UUID[3];
    project_names TEXT[3] := ARRAY['First project', 'Second project', 'Fourth project'];
    suggestion_counts INT[3] := ARRAY[1, 2, 4];
    i INT;
    j INT;
    k INT;
    project_id UUID;
    suggestion_id UUID;
    fake_user_id UUID;
    like_id UUID;
    likes_count INT;
BEGIN
    -- Проверка существования пользователя
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = user_uuid) THEN
        RAISE EXCEPTION 'User with UUID % not found', user_uuid;
    END IF;

    -- Создание 3 проектов
    FOR i IN 1..3 LOOP
        project_id := uuid_generate_v4();

        INSERT INTO projects (id, owner_id, name, description)
        VALUES (project_id, user_uuid, project_names[i], 'test description');

        -- Права владельца (админ)
        INSERT INTO project_rights (id, user_id, project_id, is_admin, votes_left)
        VALUES (uuid_generate_v4(), user_uuid, project_id, true, 1);

        project_ids[i] := project_id;

        RAISE NOTICE 'Created project: %', project_names[i];
    END LOOP;

    -- Создание предложений и лайков
    FOR i IN 1..3 LOOP
        project_id := project_ids[i];

        FOR j IN 1..suggestion_counts[i] LOOP
            suggestion_id := uuid_generate_v4();

            INSERT INTO suggestions (
                id, user_id, project_id, placed_at, last_edit,
                name, description, status
            ) VALUES (
                suggestion_id,
                user_uuid,
                project_id,
                NOW(),
                NOW(),
                format('Suggestion %s for %s', j, project_names[i]),
                'test suggestion description',
                'new'
            );

            -- Случайное количество лайков (0–10)
            likes_count := floor(random() * 11)::INT;

            FOR k IN 1..likes_count LOOP
                INSERT INTO likes (id, user_id, suggestion_id, placed_at)
                VALUES (uuid_generate_v4(), user_uuid, suggestion_id, NOW());
            END LOOP;

            RAISE NOTICE 'Added suggestion %s with %s likes to project %s', j, likes_count, project_names[i];
        END LOOP;
    END LOOP;

    RAISE NOTICE 'Successfully seeded data for user %', user_uuid;
END $$;
