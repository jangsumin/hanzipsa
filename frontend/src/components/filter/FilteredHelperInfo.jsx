import styled from 'styled-components';
import Image from '../common/Image';
import CheckButton from '../common/CheckButton';
import Paragraph from '../common/Paragraph';
import GradeBadge from '../common/GradeBadge';
import ScoreBadge from '../common/ScoreBadge';

const Wrapper = styled.div`
  cursor: pointer;
  box-sizing: border-box;
  width: 100%;
  height: 125px;
  padding: 13px 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 400;
  font-size: 16px;
  box-shadow: ${props =>
    props.$isSelected ? '0 0 0 2px #629af9 inset' : 'none'};
  background-color: #ffffff;
  border-radius: 25px;
`;

const HelperContent = styled.div`
  width: 155px;
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
`;

const HelperName = styled.div`
  width: 100%;
  font-size: 15px;
  font-weight: 700;
`;

const HelperInfos = styled.div`
  // GradeBadge : 집사 등급
  // AvgScore : 평점 (kindness_average, skill_average, rewind_average의 평균 값)
  // ReviewCount : 리뷰 총 개수
  width: 100%;
  height: 18px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 4px;
  font-size: 12px;
`;

function FilteredHelperInfo({
  zipsaId,
  profileImage,
  name,
  gradeName,
  scoreAverage,
  serviceCount,
  categories,
  onClick,
  isSelected,
}) {
  return (
    <Wrapper onClick={onClick} $isSelected={isSelected}>
      <Image
        src={`${process.env.PUBLIC_URL}/images/profile_img.svg`}
        width={'69px'}
        height={'69px'}
      ></Image>

      <HelperContent>
        <HelperName>{name}</HelperName>
        <HelperInfos>
          <GradeBadge grade={gradeName}></GradeBadge>
          <ScoreBadge score={scoreAverage} actCount={serviceCount}></ScoreBadge>
        </HelperInfos>

        <Paragraph
          fontSize="13px"
          sentences={['# 산책하기 # 뛰기 # 날기', '# 널뛰기 # 잠자기']}
        ></Paragraph>
      </HelperContent>

      <Image
        src={`${process.env.PUBLIC_URL}/images/right_arrow.svg`}
        width={'24px'}
        height={'24px'}
        margin={'0 -8px 0 0'}
      ></Image>
    </Wrapper>
  );
}

export default FilteredHelperInfo;