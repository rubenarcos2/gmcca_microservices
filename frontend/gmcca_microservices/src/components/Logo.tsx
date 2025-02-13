import Image from 'next/image';
import logo from '../assets/img/icons/gesmerca.png';

export default function Logo() {
  return (
    <Image src={logo} width={50} height={50} alt="Logo" style={{ marginTop: 5, marginRight: 20 }} />
  );
}
