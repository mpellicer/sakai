    create table lesson_builder_comments (
        id bigint generated by default as identity (start with 1),
        itemId bigint not null,
        pageId bigint not null,
        timePosted timestamp not null,
        author varchar(99) not null,
        commenttext longvarchar,
        UUID varchar(36) not null,
        html bit not null,
        points double,
        primary key (id)
    );

    create table lesson_builder_groups (
        id bigint generated by default as identity (start with 1),
        itemId varchar(255) not null,
        groupId varchar(99) not null,
        groups longvarchar,
        siteId varchar(99),
        primary key (id)
    );

    create table lesson_builder_items (
        id bigint generated by default as identity (start with 1),
        pageId bigint not null,
        sequence integer not null,
        type integer not null,
        sakaiId varchar(250),
        name varchar(100),
        html longvarchar,
        description longvarchar,
        height varchar(8),
        width varchar(8),
        alt varchar(500),
        nextPage bit,
        format varchar(255),
        required bit,
        alternate bit,
        prerequisite bit,
        subrequirement bit,
        requirementText varchar(20),
        sameWindow bit,
        groups longvarchar,
        anonymous bit,
        showComments bit,
        forcedCommentsAnonymous bit,
        showPeerEval bit,
        gradebookId varchar(100),
        gradebookPoints integer,
        gradebookTitle varchar(200),
        altGradebook varchar(100),
        altPoints integer,
        altGradebookTitle varchar(200),
        groupOwned bit,
        ownerGroups longvarchar,
        attributeString longvarchar,
        primary key (id)
    );

    create table lesson_builder_log (
        id bigint generated by default as identity (start with 1),
        lastViewed timestamp not null,
        itemId bigint not null,
        userId varchar(99) not null,
        firstViewed timestamp not null,
        complete bit not null,
        dummy bit not null,
        path varchar(255),
        toolId varchar(99),
        studentPageId bigint,
        primary key (id)
    );

    create table lesson_builder_p_eval_results (
        PEER_EVAL_RESULT_ID bigint generated by default as identity (start with 1),
        PAGE_ID bigint not null,
        TIME_POSTED timestamp,
        GRADER varchar(99) not null,
        GRADEE varchar(99) not null,
        ROW_TEXT varchar(255) not null,
        COLUMN_VALUE integer not null,
        SELECTED bit,
        primary key (PEER_EVAL_RESULT_ID)
    );

    create table lesson_builder_pages (
        pageId bigint generated by default as identity (start with 1),
        toolId varchar(99) not null,
        siteId varchar(99) not null,
        title varchar(100) not null,
        parent bigint,
        topParent bigint,
        hidden bit,
        releaseDate timestamp,
        gradebookPoints double,
        owner varchar(99),
        groupOwned bit,
        owned bit,
        groupid varchar(99),
        cssSheet varchar(250),
        primary key (pageId)
    );

    create table lesson_builder_properties (
        id bigint generated by default as identity (start with 1),
        attribute varchar(255) not null,
        value longvarchar,
        primary key (id),
        unique (attribute)
    );

    create table lesson_builder_q_responses (
        id bigint generated by default as identity (start with 1),
        timeAnswered timestamp not null,
        questionId bigint not null,
        userId varchar(99) not null,
        correct bit not null,
        shortanswer longvarchar,
        multipleChoiceId bigint,
        originalText longvarchar,
        overridden bit not null,
        points double,
        primary key (id)
    );

    create table lesson_builder_qr_totals (
        id bigint generated by default as identity (start with 1),
        questionId bigint,
        responseId bigint,
        respcount bigint,
        primary key (id)
    );

    create table lesson_builder_student_pages (
        id bigint generated by default as identity (start with 1),
        lastUpdated timestamp not null,
        itemId bigint not null,
        pageId bigint not null,
        title varchar(100) not null,
        owner varchar(99) not null,
        groupOwned bit not null,
        groupid varchar(99),
        commentsSection bigint,
        lastCommentChange timestamp,
        deleted bit,
        points double,
        primary key (id)
    );
