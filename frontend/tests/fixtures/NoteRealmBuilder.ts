import { merge } from "lodash";
import { NoteRealm, Thing } from "@/generated/backend";
import Builder from "./Builder";
import LinkViewedBuilder from "./LinkViewedBuilder";
import NoteBuilder from "./NoteBuilder";
import NotePositionBuilder from "./NotePositionBuilder";

class NoteRealmBuilder extends Builder<NoteRealm> {
  data: NoteRealm;

  noteBuilder;

  notePositionBuilder;

  constructor() {
    super();
    this.noteBuilder = new NoteBuilder();
    const noteData = this.noteBuilder.data;
    this.notePositionBuilder = new NotePositionBuilder().for(noteData);
    this.data = {
      id: noteData.id,
      note: noteData,
      links: {},
      children: [],
      notePosition: this.notePositionBuilder.data,
    };
  }

  topicConstructor(value: string): NoteRealmBuilder {
    this.noteBuilder.topicConstructor(value);
    return this;
  }

  inCircle(circleName: string) {
    this.notePositionBuilder.inCircle(circleName);
    return this;
  }

  wikidataId(value: string): NoteRealmBuilder {
    this.noteBuilder.wikidataId(value);
    return this;
  }

  details(value: string): NoteRealmBuilder {
    this.noteBuilder.details(value);
    return this;
  }

  picture(value: string): NoteRealmBuilder {
    this.noteBuilder.picture(value);
    return this;
  }

  under(value: NoteRealm): NoteRealmBuilder {
    value?.children?.push(this.data.note);
    this.data.note.parentId = value.id;

    return this;
  }

  updatedAt(value: Date): NoteRealmBuilder {
    this.noteBuilder.updatedAt(value);
    return this;
  }

  linkToSomeNote(title: string): NoteRealmBuilder {
    return this.linkTo(new NoteRealmBuilder().topicConstructor(title).do());
  }

  linkTo(note: NoteRealm): NoteRealmBuilder {
    merge(
      this.data.links,
      new LinkViewedBuilder(Thing.linkType.USING, this.data, note).please(),
    );
    return this;
  }

  do(): NoteRealm {
    this.data.note = this.noteBuilder.do();
    this.data.notePosition = this.notePositionBuilder.do();
    return this.data;
  }
}

export default NoteRealmBuilder;
