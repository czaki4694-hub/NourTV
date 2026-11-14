import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { initializeApp } from 'firebase/app';
import { getAuth, onAuthStateChanged, signInWithEmailAndPassword, signOut } from 'firebase/auth';
import { getFirestore, collection, addDoc, deleteDoc, doc, onSnapshot, query, orderBy } from 'firebase/firestore';
import './style.css';

const config = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID
};
const configured = Object.values(config).every(Boolean);
const app = configured ? initializeApp(config) : null;
const auth = app ? getAuth(app) : null;
const db = app ? getFirestore(app) : null;

function App() {
  const [user, setUser] = useState(null);
  const [channels, setChannels] = useState([]);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [form, setForm] = useState({ name: '', category: 'Moroccan', streamUrl: '', logoUrl: '' });
  const [error, setError] = useState('');

  useEffect(() => auth ? onAuthStateChanged(auth, setUser) : undefined, []);
  useEffect(() => {
    if (!user || !db) return;
    return onSnapshot(query(collection(db, 'channels'), orderBy('name')), snap =>
      setChannels(snap.docs.map(d => ({ id: d.id, ...d.data() }))),
      e => setError(e.message)
    );
  }, [user]);

  if (!configured) return <main className="panel"><h1>NourTV Admin</h1><p>أضف إعدادات Firebase إلى ملف .env لتفعيل لوحة الإدارة.</p><p>راجع README.md لإعداد Firebase Authentication وFirestore.</p></main>;
  async function login(e) { e.preventDefault(); setError(''); try { await signInWithEmailAndPassword(auth, email, password); } catch (e) { setError(e.message); } }
  async function add(e) {
    e.preventDefault(); setError('');
    try { await addDoc(collection(db, 'channels'), { ...form, enabled: true, createdAt: Date.now() }); setForm({ name:'', category:'Moroccan', streamUrl:'', logoUrl:'' }); }
    catch(e) { setError(e.message); }
  }
  if (!user) return <main className="panel"><h1>NourTV <span>ADMIN</span></h1><p>تسجيل دخول المشرف</p><form onSubmit={login}><input type="email" placeholder="البريد الإلكتروني" value={email} onChange={e=>setEmail(e.target.value)} required/><input type="password" placeholder="كلمة المرور" value={password} onChange={e=>setPassword(e.target.value)} required/><button>دخول</button></form>{error && <p className="error">{error}</p>}</main>;
  return <main className="wrap"><header><div><h1>NourTV <span>ADMIN</span></h1><p>إدارة القنوات والبث المباشر</p></div><button className="ghost" onClick={()=>signOut(auth)}>تسجيل الخروج</button></header>
    <section className="panel"><h2>إضافة قناة</h2><form className="grid" onSubmit={add}>
      <input placeholder="اسم القناة" value={form.name} onChange={e=>setForm({...form,name:e.target.value})} required/>
      <select value={form.category} onChange={e=>setForm({...form,category:e.target.value})}><option value="Moroccan">مغربية</option><option value="News">أخبار</option><option value="Sports">رياضة</option><option value="General">عامة</option></select>
      <input type="url" placeholder="رابط M3U8 المصرح به" value={form.streamUrl} onChange={e=>setForm({...form,streamUrl:e.target.value})} required/>
      <input type="url" placeholder="رابط الشعار (اختياري)" value={form.logoUrl} onChange={e=>setForm({...form,logoUrl:e.target.value})}/>
      <button>إضافة القناة</button>
    </form>{error && <p className="error">{error}</p>}</section>
    <section className="panel"><h2>القنوات ({channels.length})</h2>{channels.map(c=><article className="channel" key={c.id}><div><b>{c.name}</b><small>{c.category} · {c.streamUrl}</small></div><button className="danger" onClick={()=>deleteDoc(doc(db,'channels',c.id))}>حذف</button></article>)}</section>
  </main>;
}
createRoot(document.getElementById('root')).render(<App/>);
