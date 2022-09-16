package com.example.week7project.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

// 사용하지 않음 db수정을 해야해서 일단 남겨둠
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SessionList {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
}
